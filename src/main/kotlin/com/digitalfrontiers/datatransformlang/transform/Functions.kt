package com.digitalfrontiers.datatransformlang.transform

typealias Function<I, O> = (input: I) -> O

private val functions: MutableMap<String, Function<*, *>> = mutableMapOf()

fun <I : Any, O : Any?>registerFunction(fid: String, f: Function<I, O>) {
    functions[fid] = f
}

fun <I : Any, O : Any?>getFunction(fid: String): Function<I, O>? {
    return functions.getOrDefault(fid, null) as Function<I, O>
}
