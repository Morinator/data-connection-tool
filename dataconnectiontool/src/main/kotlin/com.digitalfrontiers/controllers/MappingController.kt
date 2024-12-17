package com.digitalfrontiers.controllers

import com.digitalfrontiers.persistence.TransformationJPARepository
import com.digitalfrontiers.persistence.createEntry
import com.digitalfrontiers.persistence.deleteEntry
import com.digitalfrontiers.persistence.updateEntry
import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.transform.Transformation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v1/mappings")
class MappingController @Autowired constructor(
    private val mappingService: MappingService,
    private val transformationJPARepository: TransformationJPARepository
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
        val id = transformationJPARepository.createEntry(transformation) // transformationRepository.save(transformation)

        val path = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as String

        val location = ServletUriComponentsBuilder
            .fromPath(path)
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
    fun getAllTransformations(): List<Any> { // TODO: Mapping instead of Any
        return transformationJPARepository.findAll().toList()
    }

    @GetMapping("/{id}")
    fun getOneMapping(@PathVariable id: Long): ResponseEntity<Transformation> { // TODO: Mapping instead of Transformation
        val entry = transformationJPARepository.findById(id).getOrNull() // transformationRepository.getById(id)?.data

        return if (entry != null) {
            ResponseEntity.ok(entry.data)
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
        val wasUpdated = transformationJPARepository.updateEntry(id, transformation)

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
        val wasDeleted = transformationJPARepository.deleteEntry(id)

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
        val transformation = transformationJPARepository.findById(id).getOrNull()?.data

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