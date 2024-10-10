package com.digitalfrontiers.dataconnectiontool.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

sealed class Modification {
    data class RenameField(val path: String, val newName: String) : Modification()
    data class ChangeValue(val path: String, val newValue: Any) : Modification()
}

object JSONModificationService {

    fun applyModifications(jsonNode: JsonNode, modifications: List<Modification>): JsonNode {
        var modifiedNode: JsonNode = jsonNode
        for (modification in modifications) {
            modifiedNode = when (modification) {
                is Modification.RenameField -> renameField(modifiedNode, modification.path, modification.newName)
                is Modification.ChangeValue -> changeValue(modifiedNode, modification.path, modification.newValue)
            }
        }
        return modifiedNode
    }

    private fun renameField(jsonNode: JsonNode, path: String, newName: String): JsonNode {
        val (parent: JsonNode, fieldName: String) = getParentAndField(jsonNode, path)
        if (parent is ObjectNode) {
            val value = parent.remove(fieldName)
            parent.set<JsonNode>(newName, value)
        }
        return jsonNode
    }

    private fun changeValue(jsonNode: JsonNode, path: String, newValue: Any): JsonNode {
        val (parent: JsonNode, fieldName: String) = getParentAndField(jsonNode, path)
        if (parent is ObjectNode) {
            when (newValue) {
                is Int -> parent.put(fieldName, newValue)
                is String -> parent.put(fieldName, newValue)
                is Boolean -> parent.put(fieldName, newValue)
            }
        }
        return jsonNode
    }

    private fun getParentAndField(jsonNode: JsonNode, path: String): Pair<JsonNode, String> {
        val parts = path.split(".")

        var current = jsonNode
        for (i in 0 until parts.size - 1) {
            current = current.path(parts[i])
        }
        return Pair(current, parts.last())
    }
}