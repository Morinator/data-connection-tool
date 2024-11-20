package com.digitalfrontiers.dataconnectiontool.controllers

import com.digitalfrontiers.dataconnectiontool.services.MappingService
import com.digitalfrontiers.dataconnectiontool.util.parseTransformNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import com.fasterxml.jackson.databind.JsonNode

@RestController
@RequestMapping("mappings")
class MappingController(
    @Autowired private val mappingService: MappingService
) {

    @PostMapping("/validate")
    fun validateMapping(@RequestBody body: MappingRequestBody) {
        TODO()
    }

    @PostMapping("/invoke")
    fun invokeMapping(@RequestBody body: MappingRequestBody) {
        mappingService.map(body.source, body.sink, parseTransformNode(body.spec))
    }
}

data class MappingRequestBody(
    val source: String,
    val sink: String,
    val spec: JsonNode
)