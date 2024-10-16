package com.digitalfrontiers.dataconnectiontool.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.file.Files

data class ProcessingStepConfig(
    val name: String,
    val description: String,
    val stepType: String,
    val args: Map<String, String>
)

data class DataframeProcessingStep(
    val name: String,
    val description: String,
    val step: (DataFrame<*>) -> DataFrame<*>
)

@RestController
class KotlinDataframeController {

    private val processingSteps: List<DataframeProcessingStep> = loadProcessingSteps()

    private fun loadProcessingSteps(): List<DataframeProcessingStep> {
        val mapper = jacksonObjectMapper()
        val configFile = ClassPathResource("processing-steps.json").inputStream
        val stepConfigs: List<ProcessingStepConfig> = mapper.readValue(configFile)

        return stepConfigs.map { config ->
            DataframeProcessingStep(
                name = config.name,
                description = config.description,
                step = when (config.stepType) {
                    "filter" -> { df ->
                        val column = config.args["column"] ?: throw IllegalArgumentException("Column not specified for filter step")
                        val condition = config.args["condition"] ?: throw IllegalArgumentException("Condition not specified for filter step")
                        df.filter { column<Int>().let { it > 100000 } } // Note: This is still hardcoded for simplicity
                    }
                    "rename" -> { df ->
                        val oldName = config.args["oldName"] ?: throw IllegalArgumentException("Old name not specified for rename step")
                        val newName = config.args["newName"] ?: throw IllegalArgumentException("New name not specified for rename step")
                        df.rename(oldName to newName)
                    }
                    else -> throw IllegalArgumentException("Unknown step type: ${config.stepType}")
                }
            )
        }
    }

    @PostMapping("/porsche")
    fun handlePorscheData(@RequestBody jsonString: String): ResponseEntity<String> {
        var tempFile: File? = null
        try {
            tempFile = Files.createTempFile("porsche_data", ".json").toFile()
            tempFile.writeText(jsonString)
            var df = DataFrame.readJson(tempFile.path)

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