package com.digitalfrontiers.dataconnectiontool.components

interface CustomFunction {
    val id: String

    val implementation: (List<Any?>) -> Any?
}