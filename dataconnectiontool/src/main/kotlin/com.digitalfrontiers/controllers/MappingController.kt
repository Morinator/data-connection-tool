package com.digitalfrontiers.controllers

import com.digitalfrontiers.persistence.SpecificationRepository
import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.util.parseTransformNode
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/mappings")
class MappingController(
    @Autowired private val mappingService: MappingService,
) {

    private val specificationRepository =  SpecificationRepository()

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    fun validateMapping(@RequestBody body: MappingRequestBody): Map<String, Boolean> =
        mapOf("isValid" to mappingService.validate(body.source, body.sink, parseTransformNode(body.spec)))

    @PostMapping("/invoke")
    @ResponseStatus(HttpStatus.OK)
    fun invokeMapping(@RequestBody body: MappingRequestBody): Map<String, Any> {
        return try {
            mappingService.map(body.source, body.sink, parseTransformNode(body.spec) as Record)
            mapOf("success" to true)
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "An unknown error occurred")
            )
        }
    }

    @PostMapping("/stored/save")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveSpecification(@RequestBody body: SaveSpecificationRequest): Map<String, Any> {
        return try {
            val specification = parseTransformNode(body.spec)
            val id = specificationRepository.save(specification)
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

    @PostMapping("/stored/invoke/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun invokeStoredMapping(
        @PathVariable id: Long,
        @RequestBody body: StoredMappingRequestBody
    ): Map<String, Any> = try {
        val specification = specificationRepository.getById(id)
            ?: throw IllegalArgumentException("No specification found with id: $id")

        val record = specification.data as? Specification.Record
            ?: throw IllegalArgumentException("Stored specification is not a valid Record type")

        mappingService.map(body.source, body.sink, record)
        mapOf("success" to true)
    } catch (e: Exception) {
        mapOf(
            "success" to false,
            "error" to (e.message ?: "An unknown error occurred")
        )
    }
}

data class MappingRequestBody(
    val source: String,
    val sink: String,
    val spec: JsonNode,
)

data class SaveSpecificationRequest(
    val spec: JsonNode
)

data class StoredMappingRequestBody(
    val source: String,
    val sink: String
)