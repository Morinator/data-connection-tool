package com.digitalfrontiers.components

import com.digitalfrontiers.Format

interface ISource {
    val id: String

    val format: Format

    fun fetch(): Map<String, String>
}

interface ISink {
    val id: String

    val format: Format

    fun put(data: Map<String, String>)
}

interface ICustomFunction {
    val id: String

    fun implementation(args: List<Any?>): Any?
}