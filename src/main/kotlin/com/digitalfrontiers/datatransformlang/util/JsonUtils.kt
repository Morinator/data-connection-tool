package com.digitalfrontiers.datatransformlang.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.JsonPath

internal val mapper = jacksonObjectMapper()

object JsonUtils{

    /**
     * @return True if [string] is a valid JSON Path.
     */
    fun isJSONPath(string: String): Boolean {
        if (string.startsWith("$"))
        {
            try {
                JsonPath.compile(string) // throws exception if it is not a valid JsonPath
                return true
            } catch (e: InvalidPathException) {
                return false
            }
        } else
        {
            return false
        }
    }

    fun toJsonString(json: Any?): String = mapper.writeValueAsString(json)

    fun unbox(node: JsonNode?): Any? = mapper.treeToValue(node, Object::class.java)
}