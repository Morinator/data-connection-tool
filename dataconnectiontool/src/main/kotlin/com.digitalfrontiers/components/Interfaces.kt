package com.digitalfrontiers.components

interface Source {
    val id: String

    val format: Format

    fun fetch(): Map<String, String>
}

interface Sink {
    val id: String

    val format: Format

    fun put(data: Map<String, String>)
}

interface CustomFunction {
    val id: String

    fun implementation(args: List<Any?>): Any?
}