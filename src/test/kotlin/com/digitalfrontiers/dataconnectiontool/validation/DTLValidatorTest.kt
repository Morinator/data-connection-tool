package com.digitalfrontiers.dataconnectiontool.validation

import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.Specification.Fetch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DTLValidatorTest {

    @Test
    fun testConstTransform() {
        val schema: String = """
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

        val transform = Specification.ToObject(
            "name" to Fetch("bli"),
            "age" to Fetch("bla"),
        )

        assertTrue(DTLValidator.compareDTLWithJSONSchema(schema, transform))
    }
}


