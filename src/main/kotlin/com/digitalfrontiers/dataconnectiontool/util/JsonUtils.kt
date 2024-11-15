package com.digitalfrontiers.dataconnectiontool.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.databind.node.*

object JsonUtils {
    private val mapper = jacksonObjectMapper()

    fun toJsonString(json: Any?): String = mapper.writeValueAsString(json)

    fun unbox(node: JsonNode?): Any? {
        return when (node) {
            null -> null
            is IntNode -> node.intValue()
            is LongNode -> node.longValue()
            is DoubleNode -> node.doubleValue()
            is BooleanNode -> node.booleanValue()
            is TextNode -> node.textValue()
            is NullNode -> null
            is ObjectNode -> node
            is ArrayNode -> node.map { unbox(it) }
            else -> throw IllegalArgumentException("Unsupported JsonNode type: ${node?.javaClass?.name}")
        }
    }
}