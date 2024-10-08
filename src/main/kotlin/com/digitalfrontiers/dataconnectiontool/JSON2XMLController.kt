package com.digitalfrontiers.dataconnectiontool

import com.digitalfrontiers.dataconnectiontool.datamapping.MappingRule
import com.digitalfrontiers.dataconnectiontool.datamapping.MappingService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JSON2XMLController(private val mappingService: MappingService) {

    @PostMapping("/convert", produces = [MediaType.APPLICATION_XML_VALUE])
    fun convertJsonToXml(@RequestBody jsonNode: JsonNode): ResponseEntity<JsonNode> {

        val mappingRules = listOf<MappingRule>(
//            RenameFieldRule("name", "fullName"),
//            TransformValueRule("age", Transformations.double),
//            RenameFieldRule("email", "contactEmail"),
//            TransformValueRule("fullName", Transformations.upperCase)
        )

        val transformedJson = mappingService.applyMapping(jsonNode, mappingRules)

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_XML)
            .body(transformedJson)
    }
}
