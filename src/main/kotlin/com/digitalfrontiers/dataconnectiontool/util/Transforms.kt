package com.digitalfrontiers.dataconnectiontool.util

import com.digitalfrontiers.datatransformlang.transform.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

fun parseTransformConfig(specString: String): Specification {
    val mapper = ObjectMapper()
    val configNode = mapper.readTree(specString)
    return parseTransformNode(configNode)
}

fun parseTransformNode(node: JsonNode): Specification {
    return when (val type = node.get("type").asText()) {
        "Const" -> ToConst(node.get("value"))
        "Fetch" -> Specification.ToInput(node.get("path").asText())
        "ToArray" -> {
            val items = node.get("items").map { parseTransformNode(it) }
            ToArray(items)
        }
        "ToObject" -> {
            val entries = node.get("entries").fields().asSequence()
                .map { (key, value) -> key to parseTransformNode(value) }
                .toMap()
            ToObject(entries)
        }
        "ForEach" -> ForEach(parseTransformNode(node.get("mapping")))
        "Call" -> {
            val fid = node.get("fid").asText()
            val args = node.get("args").map { parseTransformNode(it) }
            Call(fid, args)
        }
        "Compose" -> {
            val steps = node.get("steps").map { parseTransformNode(it) }
            Compose(steps)
        }
        else -> throw IllegalArgumentException("Unknown transform type: $type")
    }
}