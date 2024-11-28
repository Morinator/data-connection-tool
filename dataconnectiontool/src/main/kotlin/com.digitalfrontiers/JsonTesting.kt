package com.digitalfrontiers

import com.digitalfrontiers.components.JSONSource
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Specification

fun main() {
    val jsonSource = JSONSource()

    val flattenedMap = jsonSource.fetch()
    println(flattenedMap)

    val spec = Specification.Record {
        "john_name" to Input("name")
        "father_name" to Input("""father_name""")
    }

    val result = Transform.to { spec }.apply(flattenedMap)
    println(result)
}