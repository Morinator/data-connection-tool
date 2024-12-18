package com.digitalfrontiers.transform

import com.digitalfrontiers.CustomFunction
import com.digitalfrontiers.transform.Transformation.Tuple
import com.digitalfrontiers.transform.Transformation.Const
import com.digitalfrontiers.transform.Transformation.Input
import com.digitalfrontiers.util.JsonUtils
import com.jayway.jsonpath.JsonPath

// Types

internal typealias Data = Any? // Semantic(TM) Code
internal typealias Dict<T> = Map<String, T>

/**
 * Sealed class that represents different types of data transformation specifications.
 * These specifications can be used to transform input data into desired output data.
 *
 * The main usage of this class is to define a declarative way of describing how data should be transformed.
 * The [applyTransform] function can then be used to execute the transformation defined by the [Transformation].
 *
 * The [Transformation] class provides a DSL-like interface for constructing these different transformation
 * specifications in a concise and expressive way.
 */
sealed class Transformation {

    // Basic Transformations

    data object Self: Transformation()

    /**
     * Represents a constant value to be used in the transformation.
     * A common use case or hardcoded fields in API usage, like `vendor: someCompany` or `version: 1.2.3`
     *
     * @param value The stored constant value.
     */
    data class Const(val value: Data): Transformation()

    /**
     * Stores [path] to be evaluated as JSONPath.
     * See the [website](https://goessner.net/articles/JsonPath/) of JSONPath for details.
     *
     * @param path A [String] to denote a JSONPath.
     *
     * @see [isJSONPath] for validation of [path].
     */
    data class Input(val path: String): Transformation()

    /**
     * Creates a fixed sized tuple (array-like structure), where the nth item is the result of the nth specified transformation.
     */
    data class Tuple(val items: List<Transformation>): Transformation() {
        constructor(vararg items: Any?) : this(
            items
                .toList()
                .map { argToSpec(it) }
        )
    }

    /**
     *  Creates an associative array (also named `Object`, or `Dictionary` in some languages).
     *  Keys must be strings, which are used as identifiers.
     *  Can have an arbitrary size.
     */
    data class Record(val entries: Dict<Transformation>): Transformation() {
        companion object {
            operator fun invoke(setup: RecordDSL.() -> Unit): Record {
                return RecordDSL().apply(setup).getRecord()
            }
        }
    }

    // Advanced Transformations

    /**
     * Applies [mapping] when applied to a given list as data.
     *
     * A common use case is taking only a selection of fields from a list of big complex objects.
     */
    data class ListOf(val mapping: Transformation): Transformation() {
        constructor(setup: () -> Transformation): this(setup())
    }

    /**
     * Extends an existing dictionary with new key-value pairs.
     *
     * Does NOT overwrite entries if a key is already present.
     */
    data class Extension(val entries: Dict<Transformation>): Transformation() {
        companion object {
            operator fun invoke(setup: RecordDSL.() -> Unit): Extension {
                val obj = RecordDSL().apply(setup).getRecord()

                return Extension(obj.entries)
            }
        }
    }

    /**
     * Changes the values of keys of an object, specified either by a [Dict] or function of the keys.
     *
     * TODO: What should happen on unfitting input data, e.g. lists?
     */
    sealed class Rename: Transformation() {
        data class WithPairs(val pairs: Dict<String>): Rename()

        data class WithFunc(val func: (String) -> String): Rename()
    }

    /**
     * Apply a function, identified by the function id [fid].
     */
    data class ResultOf(val fid: String, val args: List<Transformation>): Transformation() {
        companion object {
            operator fun invoke(setup: ResultOfDSL.() -> ResultOf): ResultOf {
                return ResultOfDSL().setup()
            }
        }
    }

    /**
     * Compose multiple functions in the provided order, given by [steps].
     */
    data class Compose(val steps: List<Transformation>): Transformation() {
        constructor(vararg steps: Transformation): this(steps.toList())

        companion object {
            operator fun invoke(setup: DSL.() -> Compose): Compose {
                return DSL().setup()
            }
        }
    }
}

// Construction-DSLs

class DSL {

    infix fun Transformation.then(next: Transformation): Compose {
        return if (this is Compose) {
            Compose(this.steps + listOf(next))
        } else {
            Compose(listOf(this, next))
        }
    }

    infix fun Transformation.extendedWith(setup: RecordDSL.() -> Unit): Compose {
        val obj = RecordDSL().apply(setup).getRecord()

        return this then Transformation.Extension(obj.entries)
    }

    infix fun Transformation.remapping(setup: RemapDSL.() -> Map<String, String>): Compose {
        return this then Transformation.Rename.WithPairs(RemapDSL().setup())
    }

    infix fun Transformation.remappedWith(keyGen: (str: String) -> String): Compose {
        return this then Transformation.Rename.WithFunc(keyGen)
    }
}

class RecordDSL {
    private val entries = mutableMapOf<String, Transformation>()

    infix fun String.to(value: Any?) {
        if (value !is Transformation)
            entries[this] = Const(value)
        else
            entries[this] = value
    }

    infix fun String.from(path: String){
        entries[this] = Input(path)
    }

    operator fun String.invoke(setup: RecordDSL.() -> Unit) {
        entries[this] = Record(setup)
    }

    operator fun String.invoke(vararg args: Any?) {
        entries[this] = Tuple(*args as kotlin.Array<Any?>)
    }

    infix fun String.listOf(setup: () -> Transformation) {
        entries[this] = ListOf(setup())
    }

    infix fun String.resultOf(setup: ResultOfDSL.() -> ResultOf) {
        entries[this] = ResultOfDSL().setup()
    }

    fun getRecord(): Record = Record(this.entries)
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

private fun argToSpec(arg: Any?): Transformation = when {
    arg is Transformation -> arg
    arg is String && JsonUtils.isJSONPath(arg) -> Input(arg)
    else -> Const(arg)
}
// Shorthands

typealias Self = Transformation.Self
typealias Const = Transformation.Const
typealias Input = Transformation.Input
typealias Tuple = Transformation.Tuple
typealias Record = Transformation.Record
typealias ListOf = Transformation.ListOf
typealias Extension = Transformation.Extension
typealias Remap = Transformation.Rename
typealias ResultOf = Transformation.ResultOf
typealias Compose = Transformation.Compose

// Evaluation

/**
 * Applies a [Transformation] to given [Data], potentially using [customFunctions] that have to be manually defined.
 *
 * This method does most of the heavy lifting of this library, the most effort lies in defining the [Transformation],
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
 * @param spec The [Transformation] that defines what should be applied to the [data]
 * @param customFunctions A map that defines a custom function for each string identifier used as key.
 * An example might be the key "checkIfPalindrome", along with a function implementing this functionality.
 *
 * @return The result after the evaluation is done.
 */
fun applyTransform(data: Data, spec: Transformation, customFunctions: Map<String, CustomFunction> = mapOf()): Data {
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
    fun evaluate(data: Data, spec: Transformation): Data {
        return when (spec) {
            is Self -> data
            is Const -> spec.value
            is Input -> evaluateInput(data, spec)
            is Tuple -> evaluateTuple(data, spec)
            is Record -> evaluateRecord(data, spec)
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


    private fun evaluateTuple(data: Data, tupleSpec: Tuple): List<Data> {
        return tupleSpec.items.mapNotNull { evaluate(data, it) }
    }

    private fun evaluateRecord(data: Data, recordSpec: Transformation.Record): Dict<Data> {
        return recordSpec.entries.mapValues { (_, value) -> evaluate(data, value) }.filterValues { it != null } as Dict<Any>
    }

    /**
     * A specification of type [ListOf] that is evaluated on [data].
     *
     * Elements that are null will get filtered out.
     *
     * If [data] is not a list, it will be transformed and then wrapped into one.
     */
    private fun evaluateListOf(data: Data, listOfSpec: Transformation.ListOf): List<Data> =
        (data as? List<*>)
            ?.filterNotNull()
            ?.map { evaluate(it, listOfSpec.mapping) }
            ?: listOf(evaluate(data, listOfSpec.mapping))

    private fun evaluateExtension(data: Data, extensionSpec: Transformation.Extension): Dict<Data> {
        if (data is Map<*, *>) {
            val recordSpec = Record(extensionSpec.entries)

            return (data as Dict<Any>) + evaluateRecord(data, recordSpec)
        } else {
            return mapOf()
        }
    }

    private fun evaluateRemap(data: Data, rename: Transformation.Rename): Dict<Data> {

        return if (data is Map<*, *>) {
            if (rename is Transformation.Rename.WithPairs) {
                (data as Map<String, *>).mapKeys { (key, _) ->
                    rename.pairs[key] ?: key
                }
            } else {
                (data as Map<String, *>).mapKeys { (key, _) ->
                    (rename as Transformation.Rename.WithFunc).func(key)
                }
            }
        } else {
            mapOf()
        }
    }

    private fun evaluateResultOf(data: Data, resultOfSpec: Transformation.ResultOf): Data {
        val f = customFunctions.getOrDefault(resultOfSpec.fid, null) as (input: List<Any?>) -> Any? // getFunction<Any, Any>(callSpec.fid)
        val args = resultOfSpec.args.map { evaluate(data, it) }

        return f(args)
    }

    private fun evaluateCompose(data: Data, composeSpec: Transformation.Compose): Data {
        return composeSpec.steps.fold(data) { doc, step -> evaluate(doc, step) }
    }
}