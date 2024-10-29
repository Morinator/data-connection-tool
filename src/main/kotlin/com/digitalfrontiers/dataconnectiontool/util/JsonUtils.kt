package com.digitalfrontiers.dataconnectiontool.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtils {
    private val mapper = jacksonObjectMapper()

    fun toJsonNode(json: String): JsonNode = mapper.readTree(json)

    fun toJsonNode(json: Map<*, *>): JsonNode = mapper.valueToTree(json)

    fun toMapLike(data: Any): Map<*, *> {
        return mapper.convertValue(data, Map::class.java)
    }
}