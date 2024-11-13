//package com.digitalfrontiers.dataconnectiontool
//
//import org.jetbrains.kotlinx.dataframe.DataFrame
//import org.jetbrains.kotlinx.dataframe.api.*
//import org.jetbrains.kotlinx.dataframe.io.readJson
//
//fun main() {
//    val df = DataFrame.readJson("dummy_data/json/porsche_models_temp.json")
//
//    // Display basic information about the DataFrame
//    println("DataFrame columns: ${df.columnNames()}")
//
//    // Display the first few rows of the DataFrame
//    println("\nFirst few rows of the DataFrame:")
//    df.head(3).print()
//
//    // Group by vehicle type and calculate average horsepower
//    println("\nAverage horsepower by vehicle type:")
//    df.groupBy("Type")
//        .aggregate {
//            mean("Horsepower") into "Avg Horsepower"
//        }
//        .print()
//}
