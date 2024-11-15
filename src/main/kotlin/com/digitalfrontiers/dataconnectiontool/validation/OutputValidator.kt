package com.digitalfrontiers.dataconnectiontool.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage

open class OutputValidator(open val schema: String = "") {

    /**
     * True if the object conforms to the schema
     */
    fun isValid(obj: Any): Boolean {
        return getValidationMessages(obj).isEmpty()
    }

    /**
     * Get a list of validation messages. If the list is empty, the object conforms to the schema.
     */
    fun getValidationMessages(obj: Any): List<ValidationMessage> {
        val objectMapper = ObjectMapper()
        val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        val jsonSchema = jsonSchemaFactory.getSchema(objectMapper.readTree(schema))

        val parsedObject: JsonNode = objectMapper.valueToTree(obj)
        return jsonSchema.validate(parsedObject).toList()
    }
}
