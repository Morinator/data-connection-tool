package com.digitalfrontiers.dataconnectiontool.datamapping

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode

sealed class MappingRule

data class RenameFieldRule(val oldName: String, val newName: String) : MappingRule()

data class TransformValueRule(val fieldName: String, val transformation: (JsonNode) -> JsonNode) : MappingRule()

// Example transformations
object Transformations {
    val upperCase: (JsonNode) -> JsonNode = { TextNode(it.asText().uppercase()) }
    val double: (JsonNode) -> JsonNode = { IntNode(it.asInt() * 2) }
}