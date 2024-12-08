package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification

fun main() {
    val manager = TransformationDataManager()

    try {
        createTableWithEntry(manager)

        manager.getAllRows().forEach { println(it) }

        val x = manager.getById(1)
        println("\n###\nEntry with ID 1: $x")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun createTableWithEntry(manager: TransformationDataManager) {
    manager.createTable()

    manager.save(
        SpecificationEntry(
            name = "NiceName123",
            data = Specification.Record {
                "x" from "a"
                "y" from "b"
            }
        )
    )
}