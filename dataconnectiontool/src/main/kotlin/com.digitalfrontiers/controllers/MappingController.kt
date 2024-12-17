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
        return mapOf("valid" to mappingService.validate(body.source, body.sink, body.transformation))
    }

    /**
     * Create a new mapping
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveMapping(@RequestBody body: TransformationDTO): Map<String, Any> {
        val transformation = body.transformation
        val id = transformationRepository.save(transformation)

        return mapOf(
            "id" to id
        )
    }

    /**
     * Get all existing mappings
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllTransformations(): Map<String, Any> {
        return mapOf(
            "transformations" to transformationRepository.getAllRows()
        )
    }

    /**
     * Update an existing mapping
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateTransformation(
        @PathVariable id: Long,
        @RequestBody body: TransformationDTO
    ) {
        val transformation = body.transformation
        val wasUpdated = transformationRepository.update(id, transformation)

        require(wasUpdated) { "No transformation found with id: $id" }
    }

    /**
     * Delete an existing mapping
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTransformation(@PathVariable id: Long) {
        require(transformationRepository.deleteById(id)) { "No transformation found with id: $id" }
    }

    @PostMapping("/{id}/invoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun invokeStoredMapping(
        @PathVariable id: Long,
        @RequestBody body: SourceSinkDTO
    ) {
        val transformation = requireNotNull(transformationRepository.getById(id)) { "No transformation found with id: $id" }

        mappingService.map(body.source, body.sink, transformation.data as Record) // TODO: Change type in Repository (?)

        // TODO: Throw and handle better error for failed mapping (e.g. missing source/sink)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleErrorWhileParsingRequestBody(e: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "error" to "Request body contained invalid data"
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleErrorsForInvalidIds(e: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                mapOf(
                    "error" to (e.message ?: "Resource not found")
                )
            )
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