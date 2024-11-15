package com.digitalfrontiers.dataconnectiontool.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtils {
    private val mapper = jacksonObjectMapper()

    fun toJsonString(json: Any?): String = mapper.writeValueAsString(json)

    fun unbox(node: JsonNode?): Any? = mapper.treeToValue(node, Object::class.java)
}