package com.digitalfrontiers.dataconnectiontool.controller

import com.digitalfrontiers.dataconnectiontool.service.JSONModificationService
import com.digitalfrontiers.dataconnectiontool.service.Modification
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JSON2XMLController {

    @PostMapping("/convert", produces = [MediaType.APPLICATION_XML_VALUE])
    fun convertJsonToXml(@RequestBody jsonNode: JsonNode): ResponseEntity<JsonNode> {

        val modifications = listOf(
            Modification.ChangeValue("father.age", 60),
            Modification.RenameField("father", "dad"),
        )

        val modifiedJson = JSONModificationService.applyModifications(jsonNode, modifications)

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_XML)
            .body(modifiedJson)
    }

}