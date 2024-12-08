package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification

fun main() {
    val manager = NestedDataManager()

    try {
        manager.createTable()

        manager.saveNestedData(
            NestedData(
                name = "NiceName123",
                data = Specification.Record {
                    "x" from "a"
                    "y" from "b"
                }
            )
        )

        manager.allRows().forEach { println(it) }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}