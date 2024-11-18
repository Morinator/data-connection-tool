package com.digitalfrontiers.dataconnectiontool.validation

import com.digitalfrontiers.datatransformlang.transform.Record
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DTLValidatorTest {

    private val schema: String = """
        {
          "type": "object",
          "properties": {
            "name": {
              "type": "string"
            },
            "age": {
              "type": "integer"
            }
          },
          "required": ["name", "age"],
          "additionalProperties": false
        }
    """.trimIndent()

    @Test
    fun `test valid transformation`() {

        val transform = Record {
            "name" from "bli"
            "age" from "bla"
        }

        assertTrue(DTLValidator.compareDTLWithJSONSchema(schema, transform))
    }

    @Test
    fun `test invalid transformation`() {

        val transform = Record {
            "name" from "bli"
        }

        // a field is missing in the transform spec
        assertFalse(DTLValidator.compareDTLWithJSONSchema(schema, transform))
    }
}


