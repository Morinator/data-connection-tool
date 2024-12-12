package com.digitalfrontiers.services


import com.digitalfrontiers.transform.Transformation
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

/**
 * Provides serialization and deserialization from and to [Transformation] instances.
 */
@Service
class JsonService(private val objectMapper: ObjectMapper) {

    fun stringToTransformation(jsonString: String): Transformation {
        return objectMapper.readValue(jsonString, Transformation::class.java)
    }

    fun jsonNodeToTransformation(node: JsonNode): Transformation {
        return objectMapper.treeToValue(node, Transformation::class.java)
    }

    fun transformationToJson(transformation: Transformation): String {
        return objectMapper.writeValueAsString(transformation)
    }
}
