package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import com.digitalfrontiers.util.parseTransformNode
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

    @PostMapping("/start")
    fun startMapping(@RequestBody body: MappingRequestBody) {
        require(body.spec != null) {"No transformation specified!"}

        mappingService.start(body.source, body.sink, parseTransformNode(body.spec))
    }

    @PostMapping("/cancel")
    fun cancelMapping(@RequestBody body: MappingRequestBody) {
        mappingService.cancel(body.source, body.sink)
    }
}

data class MappingRequestBody(
    val source: String,
    val sink: String,
    val spec: JsonNode?
)