package com.digitalfrontiers.controllers

import com.digitalfrontiers.Mapping
import com.digitalfrontiers.persistence.MappingRepository
import com.digitalfrontiers.services.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

internal typealias ErrorInfo = Map<String, Any?>

@RestController
@RequestMapping("/api/v1/mappings")
class MappingController @Autowired constructor(
    private val mappingService: MappingService
) {

    // CRUD Operations

    /**
     * Create a new mapping
     */
    @PostMapping
    fun saveMapping(
        @RequestBody body: Mapping,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val id = mappingService.save(body) // transformationRepository.save(transformation)

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
    fun getAllMappings(): List<Mapping> {
        return mappingService.getAll()
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getMappingById(@PathVariable id: Long): Mapping = mappingService.getById(id)

    /**
     * Update an existing mapping
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMapping(
        @PathVariable id: Long,
        @RequestBody body: Mapping
    ) = mappingService.update(id, body)

    /**
     * Delete an existing mapping
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMapping(@PathVariable id: Long) = mappingService.delete(id)

    // Business Operations

    /**
     * Invoke an existing Mapping
     */
    @PostMapping("/{id}/invoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun invokeMapping(@PathVariable id: Long) = mappingService.invoke(id)

    /**
     * Validate a given Mapping
     */
    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validateMapping(@RequestBody body: Mapping) = mappingService.validate(body)

    // Error Handling

    // TODO: Delegate to ControllerAdvice instead?

    private fun createErrorInfo(e: RuntimeException): ErrorInfo {
        return mapOf(
            "error" to e.message
        )
    }

    @ExceptionHandler(MappingNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleMissingMapping(e: MappingNotFoundException): ErrorInfo = createErrorInfo(e)

    @ExceptionHandler(InvalidMappingException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleFailedValidation(e: InvalidMappingException): ErrorInfo = createErrorInfo(e)

    @ExceptionHandler(SourceNotFoundException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleMissingSource(e: SourceNotFoundException): ErrorInfo = createErrorInfo(e)

    @ExceptionHandler(SinkNotFoundException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleMissingSink(e: SinkNotFoundException): ErrorInfo = createErrorInfo(e)

}