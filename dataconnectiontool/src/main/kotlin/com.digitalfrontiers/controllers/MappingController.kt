package com.digitalfrontiers.controllers

import com.digitalfrontiers.persistence.TransformationRepository
import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/mappings")
class MappingController @Autowired constructor(
    private val mappingService: MappingService,
    val transformationRepository :  TransformationRepository
) {

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    fun validateMapping(@RequestBody body: MappingDTO): Map<String, Boolean> {
        return mapOf("isValid" to mappingService.validate(body.source, body.sink, body.transformation))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveMapping(@RequestBody body: TransformationDTO): Map<String, Any> {
        return try {
            val transformation = body.transformation
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

    @GetMapping
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

    /**
     * Update an existing transformation
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateTransformation(
        @PathVariable id: Long,
        @RequestBody body: TransformationDTO
    ): Map<String, Any> {
        return try {
            val transformation = body.transformation
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteTransformation(@PathVariable id: Long): Map<String, Boolean> {
        val wasDeleted = transformationRepository.deleteById(id)
        return mapOf("success" to wasDeleted)
    }

    @PostMapping("/{id}/invoke")
    @ResponseStatus(HttpStatus.OK)
    fun invokeStoredMapping(
        @PathVariable id: Long,
        @RequestBody body: SourceSinkDTO
    ): Map<String, Any> = try {
        val transformation = transformationRepository.getById(id)
            ?: throw IllegalArgumentException("No transformation found with id: $id")

        val record = transformation.data as? Record
            ?: throw IllegalArgumentException("Stored transformation is not a valid Record type")

        mappingService.map(body.source, body.sink, record)
        mapOf("success" to true)
    } catch (e: Exception) {
        mapOf(
            "success" to false,
            "error" to (e.message ?: "An unknown error occurred")
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleErrorWhileParsingRequestBody(e: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf(
                "error" to "Request body could not be parsed",
                "code" to "PARSE_ERROR"
            ))
    }
}

data class MappingDTO(
    val source: String,
    val sink: String,
    val transformation: Record,
)

data class TransformationDTO(
    val transformation: Record
)

data class SourceSinkDTO(
    val source: String,
    val sink: String
)