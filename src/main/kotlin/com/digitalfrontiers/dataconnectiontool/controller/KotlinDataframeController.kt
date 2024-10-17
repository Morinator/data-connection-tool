package com.digitalfrontiers.dataconnectiontool.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.file.Files
import com.jayway.jsonpath.JsonPath
data class ProcessingStepConfig(
    val name: String,
    val description: String,
    val stepType: String,
    val args: Map<String, String>
)

data class DataframeProcessingStep(
    val name: String,
    val description: String,
    val stepType: String,
    val step: (DataFrame<*>) -> DataFrame<*>
)

@RestController
class KotlinDataframeController {

    private val processingStepConfigs: List<ProcessingStepConfig> = loadProcessingStepConfigs()
    private val processingSteps: List<DataframeProcessingStep> = createProcessingSteps(processingStepConfigs)

    private fun loadProcessingStepConfigs(): List<ProcessingStepConfig> {
        val mapper = jacksonObjectMapper()
        val configFile = ClassPathResource("processing-steps.json").inputStream
        return mapper.readValue(configFile)
    }

    private fun createProcessingSteps(configs: List<ProcessingStepConfig>): List<DataframeProcessingStep> {
        return configs.map { config ->
            DataframeProcessingStep(
                name = config.name,
                description = config.description,
                stepType = config.stepType,
                step = when (config.stepType) {
                    "filter" -> { df ->
                        val column = config.args["column"]!!
                        val condition = config.args["condition"]!!
                        df.filter { column<Int>().let { it > 100000 } }
                    }
                    "rename" -> { df ->
                        val oldName = config.args["oldName"]!!
                        val newName = config.args["newName"]!!
                        df.rename(oldName to newName)
                    }
                    else -> throw IllegalArgumentException("Unknown step type: ${config.stepType}")
                }
            )
        }
    }

    @PostMapping("/porsche")
    fun handlePorscheData(@RequestBody jsonString: String): ResponseEntity<String> {

        // parse input JSON using JsonPath
        val extractedJson = JsonPath.read<Any>(jsonString, "$.porsche.models.v1").toString()

        var tempFile: File? = null
        try {
            tempFile = Files.createTempFile("porsche_data", ".json").toFile()
            tempFile.writeText(extractedJson)
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

    @GetMapping("/processing-steps")
    fun getProcessingSteps(): ResponseEntity<List<ProcessingStepConfig>> {
        return ResponseEntity.ok(processingStepConfigs)
    }
}