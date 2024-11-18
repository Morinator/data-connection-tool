package com.digitalfrontiers.olddataconnectiontool.util

import com.digitalfrontiers.datatransformlang.transform.*
import com.digitalfrontiers.datatransformlang.util.JsonUtils
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

fun parseTransformConfig(specString: String): Specification {
    val mapper = ObjectMapper()
    val configNode = mapper.readTree(specString)
    return parseTransformNode(configNode)
}

fun parseTransformNode(node: JsonNode): Specification {
    val type = node.get("type").asText()

    return when (type) {
        "Self"  -> Self
        "Const" -> Const(JsonUtils.unbox(node.get("value")))
        "Input" -> Specification.Input(node.get("path").asText())
        "Array" -> {
            val items = node.get("items").map { parseTransformNode(it) }
            Array(items)
        }
        "Object" -> {
            val entries = node.get("entries").fields().asSequence()
                .map { (key, value) -> key to parseTransformNode(value) }
                .toMap()
            Object(entries)
        }
        "ListOf" -> ListOf(parseTransformNode(node.get("mapping")))
        "ResultOf" -> {
            val fid = node.get("fid").asText()
            val args = node.get("args").map { parseTransformNode(it) }
            ResultOf(fid, args)
        }
        "Compose" -> {
            val steps = node.get("steps").map { parseTransformNode(it) }
            Compose(steps)
        }
        else -> throw IllegalArgumentException("Unknown transform type: $type")
    }
}