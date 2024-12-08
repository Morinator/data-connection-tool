package com.digitalfrontiers.persistence

fun main() {
    val manager = NestedDataManager()

    try {
        manager.createTable()

        manager.saveNestedData(
            NestedData(
                name = "deine mudda",
                data = listOf(
                    listOf(
                        mapOf<String, Any>("key1" to "value1", "key2" to 123),
                        mapOf<String, Any>("key3" to "value3", "key4" to true)
                    ),
                    listOf(
                        mapOf<String, Any>("key5" to "value5", "key6" to 456.789),
                        mapOf<String, Any>("key7" to "value7", "key8" to false)
                    )
                )
            )
        )

        // Retrieve and print all stored data
        println("\nAll stored nested data structures:")
        manager.getAllNestedData().forEach { data ->
            println("ID: ${data.id}")
            println("Name: ${data.name}")
            println("Created at: ${data.createdAt}")
            println("Data structure:")
            data.data.forEachIndexed { i, list ->
                println("  List $i:")
                list.forEachIndexed { j, map ->
                    println("    Map $j: $map")
                }
            }
            println()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}