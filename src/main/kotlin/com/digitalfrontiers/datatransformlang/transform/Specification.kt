package com.digitalfrontiers.datatransformlang.transform

import com.digitalfrontiers.datatransformlang.CustomFunction
import com.digitalfrontiers.datatransformlang.transform.Specification.Array
import com.digitalfrontiers.datatransformlang.transform.Specification.Const
import com.digitalfrontiers.datatransformlang.transform.Specification.Input
import com.digitalfrontiers.datatransformlang.util.JSON
import com.jayway.jsonpath.JsonPath

// Types

internal typealias Data = Any?; // Semantic(TM) Code
internal typealias Dict<T> = Map<String, T>;

sealed class Specification {

    // Basic Transformations

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

    data class Call(val fid: String, val args: List<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: CallDSL.() -> Call): Call {
                return CallDSL().setup()
            }
        }
    }

    data class Compose(val steps: List<Specification>): Specification() {
        constructor(vararg steps: Specification): this(steps.toList())

        companion object {
            operator fun invoke(setup: ComposeDSL.() -> Compose): Compose {
                return ComposeDSL().setup()
            }
        }
    }
}

// Construction-DSLs

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

    infix fun String.call(setup: CallDSL.() -> Call) {
        entries[this] = CallDSL().setup()
    }

    fun getToObject(): Object = Object(this.entries)
}

class CallDSL {

    operator fun String.invoke(vararg args: Any?): Call {
        val mappedArgs =
            args
                .toList()
                .map { argToSpec(it) }

        return Call(this, mappedArgs)
    }
}

class ComposeDSL {

    infix fun Specification.then(next: Specification): Compose {
        return if (this is Compose) {
            Compose(this.steps + listOf(next))
        } else {
            Compose(listOf(this, next))
        }
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

typealias Const = Specification.Const
typealias Input = Specification.Input
typealias Array = Specification.Array
typealias Object = Specification.Object
typealias ListOf = Specification.ListOf
typealias Extension = Specification.Extension
typealias Call = Specification.Call
typealias Compose = Specification.Compose

// Evaluation

fun applyTransform(data: Data, spec: Specification, customFunctions: Map<String, CustomFunction> = mapOf()): Data {
    return Handler(customFunctions).handle(data, spec)
}

private class Handler(
    private val functions: Map<String, CustomFunction>
) {
    fun handle(data: Data, spec: Specification): Data {
        return when (spec) {
            is Specification.Const -> spec.value
            is Specification.Input -> handleToInput(data, spec)
            is Array -> handleToArray(data, spec)
            is Specification.Object -> handleToObject(data, spec)
            is Specification.ListOf -> handleListOf(data, spec)
            is Specification.Extension -> handleExtend(data, spec)
            is Specification.Call -> handleCall(data, spec)
            is Specification.Compose -> handleCompose(data, spec)
        }
    }

    private inline fun handleToInput(data: Data, inputSpec: Specification.Input): Data {
        return  JsonPath.read(data, inputSpec.path)
    }


    private inline fun handleToArray(data: Data, arraySpec: Array): List<Data> {
        return arraySpec.items.mapNotNull { handle(data, it) }
    }

    private inline fun handleToObject(data: Data, objectSpec: Specification.Object): Dict<Data> {
        return objectSpec.entries.mapValues { (_, value) -> handle(data, value) }.filterValues { it != null } as Dict<Any>
    }

    private inline fun handleListOf(data: Data, listOfSpec: Specification.ListOf): List<Data> {
        return if (data is List<*>)
            data.mapNotNull {
                if (it != null)
                    handle(it, listOfSpec.mapping)
                else
                    null
            }
        else emptyList()
    }

    private inline fun handleExtend(data: Data, extensionSpec: Specification.Extension): Dict<Data> {
        if (data is Map<*, *>) {
            val objectSpec = Object(extensionSpec.entries)

            return (data as Dict<Any>) + handleToObject(data, objectSpec)
        } else {
            return mapOf()
        }
    }

    private inline fun handleCall(data: Data, callSpec: Specification.Call): Data {
        val f = functions.getOrDefault(callSpec.fid, null) as (input: List<Any?>) -> Any? // getFunction<Any, Any>(callSpec.fid)
        val args = callSpec.args.map { handle(data, it) }

        return if (f != null)
            f(args)
        else
            null
    }

    private inline fun handleCompose(data: Data, composeSpec: Specification.Compose): Data {
        return composeSpec.steps.fold(data) { doc, step -> handle(doc, step) }
    }
}