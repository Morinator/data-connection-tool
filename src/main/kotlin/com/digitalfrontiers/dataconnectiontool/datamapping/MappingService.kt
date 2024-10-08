package com.digitalfrontiers.dataconnectiontool.datamapping

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Service

@Service
class MappingService(private val objectMapper: ObjectMapper) {

    fun applyMapping(jsonNode: JsonNode, mappingRules: List<MappingRule>): JsonNode {
        val result: JsonNode = objectMapper.valueToTree(jsonNode)
        mappingRules.forEach { rule ->
            when (rule) {
                is RenameFieldRule -> renameField(result, rule)
                is TransformValueRule -> transformValue(result, rule)
            }
        }
        return result
    }

    private fun renameField(node: JsonNode, rule: RenameFieldRule) {
        if (node is ObjectNode) {
            val value = node.remove(rule.oldName)
            value?.let { node.set<JsonNode>(rule.newName, it) }
        }
    }

    private fun transformValue(node: JsonNode, rule: TransformValueRule) {
        if (node is ObjectNode && node.has(rule.fieldName)) {
            val newValue = rule.transformation(node.get(rule.fieldName))
            node.replace(rule.fieldName, newValue)
        }
    }
}