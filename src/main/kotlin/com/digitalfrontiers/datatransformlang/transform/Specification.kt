package com.digitalfrontiers.datatransformlang.transform

import com.digitalfrontiers.datatransformlang.transform.Specification.ToArray
import com.digitalfrontiers.datatransformlang.transform.Specification.ToConst
import com.digitalfrontiers.datatransformlang.transform.Specification.ToInput
import com.digitalfrontiers.datatransformlang.util.JSON
import com.jayway.jsonpath.JsonPath

// Types

internal typealias Data = Any?; // Semantic(TM) Code
internal typealias Dict<T> = Map<String, T>;

sealed class Specification {

    // Basic Transformations

    data class ToConst(val value: Data): Specification()

    data class ToInput(val path: String): Specification()

    data class ToArray(val items: List<Specification>): Specification() {
        constructor(vararg items: Any?) : this(
            items
                .toList()
                .map { argToSpec(it) }
        )
    }

    data class ToObject(val entries: Dict<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ObjectDSL.() -> Unit): ToObject {
                return ObjectDSL().apply(setup).getToObject()
            }
        }
    }

    // Advanced Transformations

    data class ForEach(val mapping: Specification): Specification() {
        constructor(setup: () -> Specification): this(setup())
    }

    data class Extend(val entries: Dict<Specification>): Specification() {
        companion object {
            operator fun invoke(setup: ObjectDSL.() -> Unit): Extend {
                val obj = ObjectDSL().apply(setup).getToObject()

                return Extend(obj.entries)
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
            entries[this] = ToConst(value)
        else
            entries[this] = value
    }

    infix fun String.from(path: String){
        entries[this] = ToInput(path)
    }

    operator fun String.invoke(setup: ObjectDSL.() -> Unit) {
        entries[this] = Specification.ToObject(setup)
    }

    operator fun String.invoke(vararg args: Any?) {
        entries[this] = ToArray(*args as Array<Any?>)
    }

    infix fun String.forEach(setup: () -> Specification) {
        entries[this] = ForEach(setup())
    }

    infix fun String.call(setup: CallDSL.() -> Call) {
        entries[this] = CallDSL().setup()
    }

    fun getToObject(): ToObject = ToObject(this.entries)
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

// Shorthands

typealias ToConst = Specification.ToConst
typealias ToInput = Specification.ToInput
typealias ToArray = Specification.ToArray
typealias ToObject = Specification.ToObject
typealias ForEach = Specification.ForEach
typealias Extend = Specification.Extend
typealias Call = Specification.Call
typealias Compose = Specification.Compose

// Evaluation

fun applyTransform(data: Data, spec: Specification): Data {
    return when (spec) {
        is Specification.ToConst -> spec.value
        is Specification.ToInput -> handleToInput(data, spec)
        is Specification.ToArray -> handleToArray(data, spec)
        is Specification.ToObject -> handleToObject(data, spec)
        is Specification.ForEach -> handleForEach(data, spec)
        is Specification.Extend -> handleExtend(data, spec)
        is Specification.Call -> handleCall(data, spec)
        is Specification.Compose -> handleCompose(data, spec)
    }
}

// Helper-Functions

private fun argToSpec(arg: Any?): Specification {
    return if (arg !is Specification)
        if (arg is String && JSON.isJSONPath(arg))
            ToInput(arg)
        else
            ToConst(arg)
    else
        arg
}

private inline fun handleToInput(data: Data, toInputSpec: Specification.ToInput): Data {
    return  JsonPath.read(data, toInputSpec.path)
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

private inline fun handleExtend(data: Data, extendSpec: Specification.Extend): Dict<Data> {
    if (data is Map<*, *>) {
        val toObjectSpec = ToObject(extendSpec.entries)

        return (data as Dict<Any>) + handleToObject(data, toObjectSpec)
    } else {
        return mapOf()
    }
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