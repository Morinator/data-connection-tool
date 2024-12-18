package com.digitalfrontiers.components


interface Source {
    val id: String

    val format: Format

    fun fetch(): List<Map<String, String>>
}
