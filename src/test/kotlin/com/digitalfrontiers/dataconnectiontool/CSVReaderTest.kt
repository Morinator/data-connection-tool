package com.digitalfrontiers.dataconnectiontool

import com.opencsv.CSVReaderBuilder
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.FileReader

@SpringBootTest
class CSVReaderTest {

    @Test
    fun contextLoads() {
    }


    @Test
    fun canReadFirstFiveLines() {
        val filePath = "dummy_data/csv/titanic.csv"

        try {
            FileReader(filePath).use { fileReader ->
                val csvReader = CSVReaderBuilder(fileReader).build()

                // Read all rows
                val allRows = csvReader.readAll()

                // Print header
                println("Header:")
                println(allRows[0].joinToString(", "))

                println("\nFirst 5 rows:")
                // Print first 5 data rows (or fewer if file has less than 5 data rows)
                for (i in 1..5) {
                    println(allRows[i].joinToString(", "))
                }
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }

    }
}
