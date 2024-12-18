package com.digitalfrontiers.controllers

import com.digitalfrontiers.persistence.TransformationRepository
import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.transform.Transformation
import com.digitalfrontiers.services.JsonService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class MappingController @Autowired constructor(
    private val mappingService: MappingService,
    val transformationRepository :  TransformationRepository,
    val jsonService: JsonService
) {

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    fun validateMapping(@RequestBody body: MappingDTO): Map<String, Boolean> =
        mapOf("isValid" to mappingService.validate(body.source, body.sink, jsonService.jsonNodeToTransformation(body.transformation)))

    @PostMapping("/invoke")
    @ResponseStatus(HttpStatus.OK)
    fun invokeMapping(@RequestBody body: MappingDTO): Map<String, Any> {
        return try {
            mappingService.map(body.source, body.sink, jsonService.jsonNodeToTransformation(body.transformation) as Record)
            mapOf("success" to true)
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "An unknown error occurred")
            )
        }
    }

    @PostMapping("/transformations/save")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveMapping(@RequestBody body: TransformationDTO): Map<String, Any> {
        return try {
            val transformation = jsonService.jsonNodeToTransformation(body.transformation)
            val id = transformationRepository.save(transformation)
            mapOf(
                "success" to true,
                "id" to id
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "An unknown error occurred")
            )
        }
    }

    @PostMapping("/transformations/{id}/invoke")
    @ResponseStatus(HttpStatus.OK)
    fun invokeStoredMapping(
        @PathVariable id: Long,
        @RequestBody body: SourceSinkDTO
    ): Map<String, Any> = try {
        val transformation = transformationRepository.getById(id)
            ?: throw IllegalArgumentException("No transformation found with id: $id")

        val record = transformation.data as? Transformation.Record
            ?: throw IllegalArgumentException("Stored transformation is not a valid Record type")

        mappingService.map(body.source, body.sink, record)
        mapOf("success" to true)
    } catch (e: Exception) {
        mapOf(
            "success" to false,
            "error" to (e.message ?: "An unknown error occurred")
        )
    }

    @DeleteMapping("/transformations/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteTransformation(@PathVariable id: Long): Map<String, Boolean> {
        val wasDeleted = transformationRepository.deleteById(id)
        return mapOf("success" to wasDeleted)
    }

    /**
     * Update an existing transformation
     */
    @PutMapping("/transformations/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateTransformation(
        @PathVariable id: Long,
        @RequestBody body: TransformationDTO
    ): Map<String, Any> {
        return try {
            val transformation = jsonService.jsonNodeToTransformation(body.transformation)
            val wasUpdated = transformationRepository.update(id, transformation)

            if (!wasUpdated) {
                throw IllegalArgumentException("No transformation found with id: $id")
            }

            mapOf("success" to true)
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "An unknown error occurred")
            )
        }
    }

    @GetMapping("/transformations")
    @ResponseStatus(HttpStatus.OK)
    fun getAllTransformations(): Map<String, Any> = try {
        mapOf(
            "success" to true,
            "transformations" to transformationRepository.getAllRows()
        )
    } catch (e: Exception) {
        mapOf(
            "success" to false,
            "error" to (e.message ?: "An unknown error occurred")
        )
    }
}

data class MappingDTO(
    val source: String,
    val sink: String,
    val transformation: JsonNode,
)

data class TransformationDTO(
    val transformation: JsonNode
)

data class SourceSinkDTO(
    val source: String,
    val sink: String
)