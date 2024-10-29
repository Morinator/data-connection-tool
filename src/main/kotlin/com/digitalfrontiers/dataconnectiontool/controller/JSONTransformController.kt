package com.digitalfrontiers.dataconnectiontool.controller

import com.digitalfrontiers.dataconnectiontool.service.StorageService
import com.digitalfrontiers.dataconnectiontool.service.TransformationService
import com.digitalfrontiers.dataconnectiontool.util.JsonUtils
import com.digitalfrontiers.dataconnectiontool.util.parseTransformConfig
import com.digitalfrontiers.dataconnectiontool.util.parseTransformNode
import com.digitalfrontiers.datatransformlang.transform.Specification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JSONTransformController(
    @Autowired private val storage: StorageService<String>,
    @Autowired private val transformer: TransformationService<String, String>
) {
    private val keyPrefix: String = "specs/"

    @PostMapping("/transforms/{id}/store")
    fun storeTransformSpec(@PathVariable id: String, @RequestBody specString: String) = storage.store("$keyPrefix$id", specString)

    @PostMapping("/transforms/{id}/apply")
    fun applyTransform(@PathVariable id: String, @RequestBody data: String): String {
        val specString = storage.load("$keyPrefix$id")

        check(specString != null) {"Failed to load transfomration specification"}

        val spec = parseTransformConfig(specString)

        return transformer.transform(data, spec)
    }

    @PostMapping("/transforms/test")
    fun test(@RequestBody data: Any): Boolean {
        val converted = JsonUtils.toMapLike(data)

        val input = converted.get("inputFormat")
        val output = converted.get("outputFormat")

        val spec = parseTransformNode(JsonUtils.toJsonNode(converted.get("spec") as Map<*, *>))

        return true
    }
}

data class TransformRequestBody(
    val inputFormat: String,
    val outputFormat: String,
    val spec: Specification
)