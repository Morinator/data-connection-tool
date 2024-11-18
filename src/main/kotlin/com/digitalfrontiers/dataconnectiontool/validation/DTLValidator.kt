package com.digitalfrontiers.dataconnectiontool.validation

import com.digitalfrontiers.dataconnectiontool.Format
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion


object DTLValidator {

    private fun getFieldsFromJSONSchema(schema: String): Set<String> {
        val objectMapper = ObjectMapper()
        val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        val jsonSchema = jsonSchemaFactory.getSchema(objectMapper.readTree(schema))

        return jsonSchema.schemaNode.get("properties").fields().asSequence().map { it.key }.toSet()
    }

    private fun getFieldsFromDTL(x: Specification.Record): Set<String> = x.entries.map { it.key }.toSet()

    /**
     * Checks if the top-level field names from the JSON-Schema String are the same as the field names defined
     * in the DTL specification.
     */
    fun validateDTLAgainstFormat(spec: Specification.Record, format: Format): Boolean {
        TODO("Implement validation against given formats")
    }
}
