package com.digitalfrontiers.components

import com.digitalfrontiers.Format

interface IEndpoint {
    val id: String

    val format: Format
}

interface ISource: IEndpoint {
    fun hasData(): Boolean
    fun fetch(): Map<String, String>
}

interface ISink: IEndpoint {
    fun put(data: Map<String, String>)
}

interface ICustomFunction {
    val id: String

    fun implementation(args: List<Any?>): Any?
}