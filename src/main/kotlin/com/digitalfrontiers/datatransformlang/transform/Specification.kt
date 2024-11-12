package com.digitalfrontiers.datatransformlang.transform

import com.digitalfrontiers.datatransformlang.CustomFunction
import com.digitalfrontiers.datatransformlang.transform.Specification.Array
import com.digitalfrontiers.datatransformlang.transform.Specification.Const
import com.digitalfrontiers.datatransformlang.transform.Specification.Input
import com.digitalfrontiers.datatransformlang.util.JSON
import com.jayway.jsonpath.JsonPath

// Types

internal typealias Data = Any? // Semantic(TM) Code
internal typealias Dict<T> = Map<String, T>

sealed class Specification {

    // Basic Transformations

    data object Self: Specification()

    data class Const(val value: Data): Specification()

    data class Input(val path: String): Specification()

    data class Array(val items: List<Specification>): Specification() {
        constructor(vararg items: Any?) : this(
            items
                .toList()
                .map { argToSpec(it) }
        )
    }

    data class Object(val entries: Dict<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ObjectDSL.() -> Unit): Object {
                return ObjectDSL().apply(setup).getToObject()
            }
        }
    }

    // Advanced Transformations

    data class ListOf(val mapping: Specification): Specification() {
        constructor(setup: () -> Specification): this(setup())
    }

    data class Extension(val entries: Dict<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ObjectDSL.() -> Unit): Extension {
                val obj = ObjectDSL().apply(setup).getToObject()

                return Extension(obj.entries)
            }
        }
    }

    sealed class Remap: Specification() {
        data class WithPairs(val pairs: Dict<String>): Remap()

        data class WithFunc(val func: (String) -> String): Remap()
    }

    data class ResultOf(val fid: String, val args: List<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ResultOfDSL.() -> ResultOf): ResultOf {
                return ResultOfDSL().setup()
            }
        }
    }

    data class Compose(val steps: List<Specification>): Specification() {
        constructor(vararg steps: Specification): this(steps.toList())

        companion object {
            operator fun invoke(setup: DSL.() -> Compose): Compose {
                return DSL().setup()
            }
        }
    }
}

// Construction-DSLs

class DSL {

    infix fun Specification.then(next: Specification): Compose {
        return if (this is Compose) {
            Compose(this.steps + listOf(next))
        } else {
            Compose(listOf(this, next))
        }
    }

    infix fun Specification.extendedWith(setup: ObjectDSL.() -> Unit): Compose {
        val obj = ObjectDSL().apply(setup).getToObject()

        return this then Specification.Extension(obj.entries)
    }

    infix fun Specification.remapping(setup: RemapDSL.() -> Map<String, String>): Compose {
        return this then Specification.Remap.WithPairs(RemapDSL().setup())
    }

    infix fun Specification.remappedWith(keyGen: (str: String) -> String): Compose {
        return this then Specification.Remap.WithFunc(keyGen)
    }
}

class ObjectDSL {
    private val entries = mutableMapOf<String, Specification>()

    infix fun String.to(value: Any?) {
        if (value !is Specification)
            entries[this] = Const(value)
        else
            entries[this] = value
    }

    infix fun String.from(path: String){
        entries[this] = Input(path)
    }

    operator fun String.invoke(setup: ObjectDSL.() -> Unit) {
        entries[this] = Specification.Object(setup)
    }

    operator fun String.invoke(vararg args: Any?) {
        entries[this] = Array(*args as kotlin.Array<Any?>)
    }

    infix fun String.listOf(setup: () -> Specification) {
        entries[this] = ListOf(setup())
    }

    infix fun String.resultOf(setup: ResultOfDSL.() -> ResultOf) {
        entries[this] = ResultOfDSL().setup()
    }

    fun getToObject(): Object = Object(this.entries)
}

class RemapDSL {
    private val entries = mutableMapOf<String, String>()

    infix fun String.to(newKey: String): Map<String, String> {
        entries[this] = newKey

        return entries.toMap()
    }
}

class ResultOfDSL {

    operator fun String.invoke(vararg args: Any?): ResultOf {
        val mappedArgs =
            args
                .toList()
                .map { argToSpec(it) }

        return ResultOf(this, mappedArgs)
    }
}

// Helper-Functions

private fun argToSpec(arg: Any?): Specification {
    return if (arg !is Specification)
        if (arg is String && JSON.isJSONPath(arg))
            Input(arg)
        else
            Const(arg)
    else
        arg
}

// Shorthands

typealias Self = Specification.Self
typealias Const = Specification.Const
typealias Input = Specification.Input
typealias Array = Specification.Array
typealias Object = Specification.Object
typealias ListOf = Specification.ListOf
typealias Extension = Specification.Extension
typealias ResultOf = Specification.ResultOf
typealias Compose = Specification.Compose

// Evaluation

fun applyTransform(data: Data, spec: Specification, customFunctions: Map<String, CustomFunction> = mapOf()): Data {
    return Evaluator(customFunctions).evaluate(data, spec)
}

private class Evaluator(
    private val customFunctions: Map<String, CustomFunction>
) {
    fun evaluate(data: Data, spec: Specification): Data {
        return when (spec) {
            is Specification.Self -> data
            is Specification.Const -> spec.value
            is Specification.Input -> evaluateInput(data, spec)
            is Array -> evaluateArray(data, spec)
            is Specification.Object -> evaluateObject(data, spec)
            is Specification.ListOf -> evaluateListOf(data, spec)
            is Specification.Extension -> evaluateExtension(data, spec)
            is Specification.Remap -> evaluateRemap(data, spec)
            is Specification.ResultOf -> evaluateResultOf(data, spec)
            is Specification.Compose -> evaluateCompose(data, spec)
        }
    }

    private fun evaluateInput(data: Data, inputSpec: Specification.Input): Data {
        return  JsonPath.read(data, inputSpec.path)
    }


    private fun evaluateArray(data: Data, arraySpec: Array): List<Data> {
        return arraySpec.items.mapNotNull { evaluate(data, it) }
    }

    private fun evaluateObject(data: Data, objectSpec: Specification.Object): Dict<Data> {
        return objectSpec.entries.mapValues { (_, value) -> evaluate(data, value) }.filterValues { it != null } as Dict<Any>
    }

    private fun evaluateListOf(data: Data, listOfSpec: Specification.ListOf): List<Data> {
        return if (data is List<*>)
            data.mapNotNull {
                if (it != null)
                    evaluate(it, listOfSpec.mapping)
                else
                    null
            }
        else emptyList()
    }

    private fun evaluateExtension(data: Data, extensionSpec: Specification.Extension): Dict<Data> {
        if (data is Map<*, *>) {
            val objectSpec = Object(extensionSpec.entries)

            return (data as Dict<Any>) + evaluateObject(data, objectSpec)
        } else {
            return mapOf()
        }
    }

    private fun evaluateRemap(data: Data, remap: Specification.Remap): Dict<Data> {

        return if (data is Map<*, *>) {
            if (remap is Specification.Remap.WithPairs) {
                (data as Map<String, *>).mapKeys { (key, _) ->
                    remap.pairs[key] ?: key
                }
            } else {
                (data as Map<String, *>).mapKeys { (key, _) ->
                    (remap as Specification.Remap.WithFunc).func(key)
                }
            }
        } else {
            mapOf()
        }
    }

    private fun evaluateResultOf(data: Data, resultOfSpec: Specification.ResultOf): Data {
        val f = customFunctions.getOrDefault(resultOfSpec.fid, null) as (input: List<Any?>) -> Any? // getFunction<Any, Any>(callSpec.fid)
        val args = resultOfSpec.args.map { evaluate(data, it) }

        return f(args)
    }

    private fun evaluateCompose(data: Data, composeSpec: Specification.Compose): Data {
        return composeSpec.steps.fold(data) { doc, step -> evaluate(doc, step) }
    }
}