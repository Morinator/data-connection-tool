package com.digitalfrontiers

import com.digitalfrontiers.transform.convert.defaults.JSONParser
import java.io.File


class JSONFlattener {
    private val jsonParser = JSONParser()

    fun flattenJsonFromFile(filePath: String): Map<String, Any?> {
        val jsonContent = File(filePath).readText()
        val parsedJson = jsonParser.parse(jsonContent)
        return flattenJson(parsedJson)
    }

    /**
     * Recursively flattens a JSON structure into a map with dot-notation paths.
     * For example, {"a": {"b": 1}} becomes {"a.b": 1}
     * Arrays are handled with index notation, e.g., {"a": [1, 2]} becomes {"a[0]": 1, "a[1]": 2}
     *
     * @param json The JSON object to flatten (can be a Map, List, or primitive value)
     * @param parentPath The accumulated path to the current object (used in recursion). Is "" by default.
     * @return A map with flattened paths as keys and their corresponding values
     */
    private fun flattenJson(json: Any?, parentPath: String = ""): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()

        when (json) {
            is Map<*, *> -> {
                json.forEach { (key, value) ->
                    // Construct the new path by appending the current key to the parent path
                    // If parentPath is empty, just use the key; otherwise use dot notation
                    val newPath = if (parentPath.isEmpty()) key.toString() else "$parentPath.$key"

                    when (value) {
                        // For nested objects (Maps) or arrays (Lists), recurse deeper
                        is Map<*, *>, is List<*> -> {
                            result.putAll(flattenJson(value, newPath))
                        }
                        // For primitive values, add them directly with the current path
                        else -> {
                            result[newPath] = value
                        }
                    }
                }
            }

            // Handle JSON arrays (represented as Lists)
            is List<*> -> {
                json.forEachIndexed { index, value ->
                    // Use bracket notation for array indices: parentPath[index]
                    val newPath = "$parentPath[$index]"

                    when (value) {
                        // For nested objects or arrays within the array, recurse deeper
                        is Map<*, *>, is List<*> -> {
                            result.putAll(flattenJson(value, newPath))
                        }
                        // For primitive values in the array, add them directly with the indexed path
                        else -> {
                            result[newPath] = value
                        }
                    }
                }
            }

            // Handle primitive values (strings, numbers, booleans, null)
            else -> {
                // Only add the primitive value if we have a parent path
                // This prevents adding a root primitive value with an empty key
                if (parentPath.isNotEmpty()) {
                    result[parentPath] = json
                }
            }
        }

        return result
    }
}

fun main() {
    val flattener = JSONFlattener()
    val flattenedJson = flattener.flattenJsonFromFile("dummy_data/json/bla.json")

    flattenedJson.forEach { (path, value) ->
        println("$path = $value")
    }
}