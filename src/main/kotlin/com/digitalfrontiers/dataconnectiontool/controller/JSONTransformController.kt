package com.digitalfrontiers.dataconnectiontool.controller

import com.digitalfrontiers.dataconnectiontool.service.IStorageService
import com.digitalfrontiers.dataconnectiontool.service.ITransformationService
import com.digitalfrontiers.dataconnectiontool.util.JsonUtils
import com.digitalfrontiers.dataconnectiontool.util.parseTransformConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transforms", consumes = [MediaType.APPLICATION_JSON_VALUE])
class JSONTransformController(
    @Autowired private val storage: IStorageService<String>,
    @Autowired private val transformer: ITransformationService<String, String>
) {
    private val keyPrefix: String = "specs/"

    @PutMapping("/{id}")
    fun storeTransformSpec(@PathVariable id: String, @RequestBody specString: String) = storage.store("$keyPrefix$id", specString)

    @PostMapping("/{id}/apply")
    fun applyTransform(@PathVariable id: String, @RequestBody body: TransformationRequestBody): String {
        val specString = storage.load("$keyPrefix$id")

        check(specString != null) {"Failed to load transformation specification"}

        val spec = parseTransformConfig(specString)

        val data: String =
            if (body.data !is String)
                JsonUtils.toJsonString(body.data)
            else
                body.data

        return transformer.transform(data, spec, body.inputFormat, body.outputFormat)
    }
}

data class TransformationRequestBody(
    val inputFormat: String?,
    val outputFormat: String?,
    val data: Any
)