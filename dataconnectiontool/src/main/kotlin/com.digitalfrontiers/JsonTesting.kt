package com.digitalfrontiers

import com.digitalfrontiers.components.JSONSink
import com.digitalfrontiers.components.JSONSource
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Specification

//TODO move to test
fun main() {
    val jsonSource = JSONSource()
    val jsonSink = JSONSink()

    val flattenedMap = jsonSource.fetch()
    println("flattenedMap:      $flattenedMap")

    val spec = Specification.Record {
        "john_name" to Input("name")
        "father_name" to Input("['father.name']")
    }

    val transformed: Map<String, String> = (Transform to { spec }).apply(flattenedMap) as Map<String, String>

    println("transformed data : $transformed")

    jsonSink.put(transformed)

}