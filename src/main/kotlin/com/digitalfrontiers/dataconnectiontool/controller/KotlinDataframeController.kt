package com.digitalfrontiers.dataconnectiontool.controller

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.file.Files

data class DataframeProcessingStep(
    val name: String,
    val description: String,
    val step: (DataFrame<*>) -> DataFrame<*>
)

@RestController
class KotlinDataframeController {

    private val processingSteps = listOf(
        DataframeProcessingStep(
            name = "drop cheap cars",
            description = "only keep cars with price over $100,000",
            step = { df -> df.filter { "Price (USD)"<Int>() > 100_000 } }
        ),
        DataframeProcessingStep(
            name = "rename horsepower",
            description = "rename column 'Horsepower' into 'HP'",
            step = { df ->
                df.rename("Horsepower" to "HP")
            }
        ),
    )

    @PostMapping("/porsche")
    fun handlePorscheData(@RequestBody jsonString: String): ResponseEntity<String> {
        var tempFile: File? = null
        try {
            // create dataframe
            tempFile = Files.createTempFile("porsche_data", ".json").toFile()
            tempFile.writeText(jsonString)
            var df = DataFrame.readJson(tempFile.path) // can't read from String (I think??)

            // Apply all processing steps sequentially
            for (step in processingSteps) {
                df = step.step(df)
            }

            return ResponseEntity.ok(df.toCsv())

        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Error processing data: ${e.message}")
        } finally {
            tempFile?.delete()
        }
    }
}