package com.digitalfrontiers.datatransformlang.transform

import com.jayway.jsonpath.JsonPath

// Types

internal typealias Data = Any?; // Semantic(TM) Code
internal typealias Dict<T> = Map<String, T>;

sealed class Specification {
    data class Const(val value: Data): Specification()

    data class Fetch(val path: String): Specification()

    data class ToArray(val items: List<Specification>): Specification() {
        constructor(vararg items: Specification) : this(items.toList())
    }

    data class ToObject(val entries: Dict<Specification>): Specification() {
        constructor(vararg entries: Pair<String, Specification>): this(mapOf(*entries))
    }

    data class ForEach(val mapping: Specification): Specification()

    data class Call(val fid: String, val args: List<Specification>): Specification() {
        constructor(fid: String, vararg args: Specification): this(fid, args.toList())
    }

    data class Compose(val steps: List<Specification>): Specification() {
        constructor(vararg steps: Specification): this(steps.toList())
    }
}

// Shorthands

typealias Const = Specification.Const
typealias Fetch = Specification.Fetch
typealias Call = Specification.Call
typealias ToArray = Specification.ToArray
typealias ToObject = Specification.ToObject
typealias ForEach = Specification.ForEach
typealias Compose = Specification.Compose

// Evaluation

fun applyTransform(data: Data, spec: Specification): Data {
    return when (spec) {
        is Specification.Const -> spec.value
        is Specification.Fetch -> handleFetch(data, spec)
        is Specification.ToArray -> handleToArray(data, spec)
        is Specification.ToObject -> handleToObject(data, spec)
        is Specification.ForEach -> handleForEach(data, spec)
        is Specification.Call -> handleCall(data, spec)
        is Specification.Compose -> handleCompose(data, spec)
    }
}

// Helper-Functions

private inline fun handleFetch(data: Data, fetchSpec: Specification.Fetch): Data {
    return  JsonPath.read(data, fetchSpec.path)
}


private inline fun handleToArray(data: Data, toArraySpec: Specification.ToArray): List<Data> {
    return toArraySpec.items.mapNotNull { applyTransform(data, it) }
}

private inline fun handleToObject(data: Data, toObjectSpec: Specification.ToObject): Dict<Data> {
    return toObjectSpec.entries.mapValues { (_, value) -> applyTransform(data, value) }.filterValues { it != null } as Dict<Any>
}

private inline fun handleForEach(data: Data, forEachSpec: Specification.ForEach): List<Data> {
    return if (data is List<*>)
        data.mapNotNull {
            if (it != null)
                applyTransform(it, forEachSpec.mapping)
            else
                null
        }
    else emptyList()
}

private inline fun handleCall(data: Data, callSpec: Specification.Call): Data {
    val f = getFunction<Any, Any>(callSpec.fid)
    val args = callSpec.args.map { applyTransform(data, it) }

    return if (f != null)
        f(args)
    else
        null
}

private inline fun handleCompose(data: Data, composeSpec: Specification.Compose): Data {
    return composeSpec.steps.fold(data) { doc, step -> applyTransform(doc, step) }
}