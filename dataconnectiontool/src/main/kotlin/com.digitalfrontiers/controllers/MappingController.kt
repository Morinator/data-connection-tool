package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.util.parseTransformNode
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("mappings")
class MappingController(
    @Autowired private val mappingService: MappingService,
) {

    @PostMapping("/validate")
    fun validateMapping(@RequestBody body: MappingRequestBody): Map<String, Boolean> =
        mapOf("isValid" to mappingService.validate(body.source, body.sink, parseTransformNode(body.spec)))

    @PostMapping("/invoke")
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
}

data class MappingRequestBody(
    val source: String,
    val sink: String,
    val spec: JsonNode,
)