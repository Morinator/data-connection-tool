package com.digitalfrontiers.dataconnectiontool.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage

object OutputValidationService {


    /**
     * True if the object conforms to the schema
     */
    fun isValid(obj: Any, schema: String): Boolean {

        return getValidationMessages(obj, schema).isNotEmpty()
    }

    /**
     * Get a list of validation messages. If the list is empty, the object conforms to the schema.
     */
    fun getValidationMessages(obj: Any, schema: String): List<ValidationMessage> {

        val objectMapper = ObjectMapper()
        val jsonSchemaFactory= JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        val jsonSchema = jsonSchemaFactory.getSchema(objectMapper.readTree(schema))


        val parsedObject: JsonNode = objectMapper.valueToTree(obj)
        return jsonSchema.validate(parsedObject).toList()
    }
}