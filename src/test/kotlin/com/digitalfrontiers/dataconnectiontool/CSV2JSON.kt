package com.digitalfrontiers.dataconnectiontool

import com.fasterxml.jackson.databind.ObjectMapper
import com.opencsv.CSVReaderBuilder
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileReader


@SpringBootTest
class CSV2JSON {


    @Test
    fun jacksonTest() {

        data class TitanicPassenger(val fields: Map<String, String>)

        val filePath = "dummy_data/csv/titanic.csv"
        val jsonOutputPath = "titanic_output.json"

        try {
            FileReader(filePath).use { fileReader ->
                val csvReader = CSVReaderBuilder(fileReader).build()

                // Read all rows
                val allRows = csvReader.readAll()

                // Extract header
                val header = allRows[0]

                // Print header
                println("Header:")
                println(header.joinToString(", "))

                println("\nFirst 5 rows:")
                // Print first 5 data rows (or fewer if file has less than 5 data rows)
                for (i in 1 until minOf(6, allRows.size)) {
                    println(allRows[i].joinToString(", "))
                }

                // Convert rows to list of TitanicPassenger objects
                val passengers = allRows.drop(1).map { row ->
                    TitanicPassenger(header.zip(row).toMap())
                }

                // Serialize to JSON
                val objectMapper = ObjectMapper()
                objectMapper.writeValue(File(jsonOutputPath), passengers)

                println("\nJSON file has been created at: $jsonOutputPath")
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
}
