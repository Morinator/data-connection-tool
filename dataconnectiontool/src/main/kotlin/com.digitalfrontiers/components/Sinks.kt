package com.digitalfrontiers.components


interface Sink {
    val id: String

    val format: Format

    fun put(data: List<Map<String, String>>)
}
