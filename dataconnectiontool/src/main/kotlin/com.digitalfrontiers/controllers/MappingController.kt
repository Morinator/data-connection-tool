package com.digitalfrontiers.controllers

import com.digitalfrontiers.persistence.TransformationRepository
import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.transform.Transformation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
        @RequestBody body: TransformationDTO
    ): ResponseEntity<Void> {
        val transformation = body.transformation
        val id = transformationRepository.save(transformation)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
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
    fun getAllTransformations(): List<Transformation> { // TODO: Mapping instead of Transformation
        return transformationRepository.getAllRows().map { it.data }
    }

    @GetMapping("/{id}")
    fun getOneMapping(@PathVariable id: Long): ResponseEntity<Transformation> { // TODO: Mapping instead of Transformation
        val t = transformationRepository.getById(id)?.data

        return if (t != null) {
            ResponseEntity.ok(t)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Update an existing mapping
     */
    @PutMapping("/{id}")
    fun updateTransformation(
        @PathVariable id: Long,
        @RequestBody body: TransformationDTO
    ): ResponseEntity<Void> {
        val transformation = body.transformation
        val wasUpdated = transformationRepository.update(id, transformation)

        return if (wasUpdated) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Delete an existing mapping
     */
    @DeleteMapping("/{id}")
    fun deleteTransformation(@PathVariable id: Long): ResponseEntity<Void> {
        val wasDeleted = transformationRepository.deleteById(id)

        return if (wasDeleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/invoke")
    fun invokeStoredMapping(
        @PathVariable id: Long,
        @RequestBody body: SourceSinkDTO
    ): ResponseEntity<Void> {
        val transformation = transformationRepository.getById(id)?.data

        return if (transformation != null) {
            mappingService.map(body.source, body.sink, transformation as Record) // TODO: Change type in Repository (?)

            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }

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