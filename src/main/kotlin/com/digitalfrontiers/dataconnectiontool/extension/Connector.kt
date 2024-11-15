package com.digitalfrontiers.dataconnectiontool.extension

abstract class Connector<I : Any, O : Any>(
    val config: ConnectionConfig
) {

    abstract fun parseInput(input: Any): I

    abstract fun validateInput(input: I): Boolean

    abstract fun transformInput(input: I): O

    abstract fun sendResult(result: O)

    fun process(input: Any) {
        val parsed = parseInput(input)

        if (validateInput(parsed)) {
            sendResult(transformInput(parsed))
        } else {
            // TODO: Throw Exception, maybe?
        }
    }
}

typealias ConnectionConfig = Any
