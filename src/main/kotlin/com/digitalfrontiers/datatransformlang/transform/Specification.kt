package com.digitalfrontiers.datatransformlang.transform

import com.digitalfrontiers.datatransformlang.CustomFunction
import com.digitalfrontiers.datatransformlang.transform.Specification.Array
import com.digitalfrontiers.datatransformlang.transform.Specification.Const
import com.digitalfrontiers.datatransformlang.transform.Specification.Input
import com.digitalfrontiers.datatransformlang.util.JsonUtils
import com.digitalfrontiers.datatransformlang.util.JsonUtils.isJSONPath
import com.jayway.jsonpath.JsonPath

// Types

internal typealias Data = Any? // Semantic(TM) Code
internal typealias Dict<T> = Map<String, T>

/**
 * Sealed class that represents different types of data transformation specifications.
 * These specifications can be used to transform input data into desired output data.
 *
 * The main usage of this class is to define a declarative way of describing how data should be transformed.
 * The [applyTransform] function can then be used to execute the transformation defined by the [Specification].
 *
 * The [Specification] class provides a DSL-like interface for constructing these different transformation
 * specifications in a concise and expressive way.
 */
sealed class Specification {

    // Basic Transformations

    data object Self: Specification()

    /**
     * Represents a constant value to be used in the transformation.
     * A common use case or hardcoded fields in API usage, like `vendor: someCompany` or `version: 1.2.3`
     *
     * @param value The stored constant value.
     */
    data class Const(val value: Data): Specification()

    /**
     * Stores [path] to be evaluated as JSONPath.
     * See the [website](https://goessner.net/articles/JsonPath/) of JSONPath for details.
     *
     * @param path A [String] to denote a JSONPath.
     *
     * @see [isJSONPath] for validation of [path].
     */
    data class Input(val path: String): Specification()

    /**
     * Creates a fixed sized array from the given elements.
     */
    data class Array(val items: List<Specification>): Specification() {
        constructor(vararg items: Any?) : this(
            items
                .toList()
                .map { argToSpec(it) }
        )
    }

    /**
     *  Creates an associative array (or named `Object` in Javascript).
     *  Keys must be strings, which are used as identifiers.
     *  Can have an arbitrary size.
     */
    data class Object(val entries: Dict<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ObjectDSL.() -> Unit): Object {
                return ObjectDSL().apply(setup).getToObject()
            }
        }
    }

    // Advanced Transformations

    /**
     * Applies [mapping] when applied to a given list as data.
     *
     * A common use case is taking only a selection of fields from a list of big complex objects.
     */
    data class ListOf(val mapping: Specification): Specification() {
        constructor(setup: () -> Specification): this(setup())
    }

    /**
     * Extends an existing dictionary with new key-value pairs.
     *
     * Does NOT overwrite entries if a key is already present.
     */
    data class Extension(val entries: Dict<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ObjectDSL.() -> Unit): Extension {
                val obj = ObjectDSL().apply(setup).getToObject()

                return Extension(obj.entries)
            }
        }
    }

    /**
     * Changes the values of keys of an object, specified either by a [Dict] or function of the keys.
     *
     * TODO: What should happen on unfitting input data, e.g. lists?
     */
    sealed class Rename: Specification() {
        data class WithPairs(val pairs: Dict<String>): Rename()

        data class WithFunc(val func: (String) -> String): Rename()
    }

    /**
     * Apply a function, identified by the function id [fid].
     */
    data class ResultOf(val fid: String, val args: List<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ResultOfDSL.() -> ResultOf): ResultOf {
                return ResultOfDSL().setup()
            }
        }
    }

    /**
     * Compose multiple functions in the provided order, given by [steps].
     */
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
        return this then Specification.Rename.WithPairs(RemapDSL().setup())
    }

    infix fun Specification.remappedWith(keyGen: (str: String) -> String): Compose {
        return this then Specification.Rename.WithFunc(keyGen)
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

private fun argToSpec(arg: Any?): Specification = when {
    arg is Specification -> arg
    arg is String && JsonUtils.isJSONPath(arg) -> Input(arg)
    else -> Const(arg)
}
// Shorthands

typealias Self = Specification.Self
typealias Const = Specification.Const
typealias Input = Specification.Input
typealias Array = Specification.Array
typealias Object = Specification.Object
typealias ListOf = Specification.ListOf
typealias Extension = Specification.Extension
typealias Remap = Specification.Rename
typealias ResultOf = Specification.ResultOf
typealias Compose = Specification.Compose

// Evaluation

/**
 * Applies a [Specification] to given [Data], potentially using [customFunctions] that have to be manually defined.
 *
 * This method does most of the heavy lifting of this library, the most effort lies in defining the [Specification],
 * potentially with its [customFunctions].
 *
 * Example usage:
 * ```kotlin
 * val grades: Data = mapOf(
 *   "michael" to 3,
 *   "hillary" to 5,
 *   "josh" to 1
 * )
 * val spec = Input("josh") // get grade for "josh"
 * val result = applyTransform(grades, spec) // equals 1
 * ```
 *
 * @param data Some input of arbitrary type
 * @param spec The [Specification] that defines what should be applied to the [data]
 * @param customFunctions A map that defines a custom function for each string identifier used as key.
 * An example might be the key "checkIfPalindrome", along with a function implementing this functionality.
 *
 * @return The result after the evaluation is done.
 */
fun applyTransform(data: Data, spec: Specification, customFunctions: Map<String, CustomFunction> = mapOf()): Data {
    return Evaluator(customFunctions).evaluate(data, spec)
}

/**
 * Groups all functions used for evaluation together. This class is primarily used in [applyTransform].
 *
 * @param customFunctions The custom functions used for evaluation, usually taken from [applyTransform].
 */
private class Evaluator(
    private val customFunctions: Map<String, CustomFunction>
) {
    fun evaluate(data: Data, spec: Specification): Data {
        return when (spec) {
            is Self -> data
            is Const -> spec.value
            is Input -> evaluateInput(data, spec)
            is Array -> evaluateArray(data, spec)
            is Object -> evaluateObject(data, spec)
            is ListOf -> evaluateListOf(data, spec)
            is Extension -> evaluateExtension(data, spec)
            is Remap -> evaluateRemap(data, spec)
            is ResultOf -> evaluateResultOf(data, spec)
            is Compose -> evaluateCompose(data, spec)
        }
    }

    /**
     * @return The part of [data] specified by the JSON path given by [inputSpec].
     */
    private fun evaluateInput(data: Data, inputSpec: Input): Data {
        return  JsonPath.read(data, inputSpec.path)
    }


    private fun evaluateArray(data: Data, arraySpec: Array): List<Data> {
        return arraySpec.items.mapNotNull { evaluate(data, it) }
    }

    private fun evaluateObject(data: Data, objectSpec: Specification.Object): Dict<Data> {
        return objectSpec.entries.mapValues { (_, value) -> evaluate(data, value) }.filterValues { it != null } as Dict<Any>
    }

    /**
     * A specification of type [ListOf] that is evaluated on [data].
     *
     * Elements that are null will get filtered out.
     *
     * If [data] is not a list, an empty list will be returned.
     */
    private fun evaluateListOf(data: Data, listOfSpec: Specification.ListOf): List<Data> =
        (data as? List<*>)
            ?.filterNotNull()
            ?.map { evaluate(it, listOfSpec.mapping) }
            ?: emptyList()

    private fun evaluateExtension(data: Data, extensionSpec: Specification.Extension): Dict<Data> {
        if (data is Map<*, *>) {
            val objectSpec = Object(extensionSpec.entries)

            return (data as Dict<Any>) + evaluateObject(data, objectSpec)
        } else {
            return mapOf()
        }
    }

    private fun evaluateRemap(data: Data, rename: Specification.Rename): Dict<Data> {

        return if (data is Map<*, *>) {
            if (rename is Specification.Rename.WithPairs) {
                (data as Map<String, *>).mapKeys { (key, _) ->
                    rename.pairs[key] ?: key
                }
            } else {
                (data as Map<String, *>).mapKeys { (key, _) ->
                    (rename as Specification.Rename.WithFunc).func(key)
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