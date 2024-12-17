package com.digitalfrontiers.controllers

import com.digitalfrontiers.persistence.TransformationRepository
import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

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
    fun saveMapping(
        @RequestBody body: TransformationDTO,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val transformation = body.transformation
        val id = transformationRepository.save(transformation)

        val location = ServletUriComponentsBuilder
            .fromRequest(request)
            .path("/{id}")
            .buildAndExpand(id)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    /**
     * Get all existing mappings
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllTransformations(): List<Any> {
        return transformationRepository.getAllRows()
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