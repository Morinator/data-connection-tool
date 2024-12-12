package com.digitalfrontiers.services


import com.digitalfrontiers.transform.Specification
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class JsonService(private val objectMapper: ObjectMapper) {

    fun stringToTransformation(jsonString: String): Specification {
        return objectMapper.readValue(jsonString, Specification::class.java)
    }

    fun jsonNodeToTransformation(node: JsonNode): Specification {
        return objectMapper.treeToValue(node, Specification::class.java)
    }

    fun transformationToJson(specification: Specification): String {
        return objectMapper.writeValueAsString(specification)
    }
}
