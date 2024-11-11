package com.digitalfrontiers.dataconnectiontool.validation

import com.networknt.schema.ValidationMessage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

class OutputValidatorTest {

    private val valid1 = mapOf(
        "vehicle_id" to "1234",
        "title" to "der gerät",
        "vehicle_type" to "boat"
    )

    private val valid2 = mapOf(
        "vehicle_id" to "1234",
        "title" to "der gerät",
        // vehicle_type is optional
    )

    private val invalid1 = mapOf(
        "vehicle_id" to "x".repeat(101), // too long
        "title" to "der gerät",
        "vehicle_type" to "boat"
    )

    private val invalid2 = mapOf(
        "vehicle_id" to "1234",
        // title missing
        "vehicle_type" to "boat"
    )

    private val carSchema: String = """
    {
        "type": "object",
        "properties": {
            "vehicle_id": {
                "type": "string",
                "maxLength": 100
            },
            "title": {
                "type": "string"
            },
            "vehicle_type": {
                "type": "string"
            }
        },
        "required": ["vehicle_id", "title"],
        "additionalProperties": false
    }
""".trimIndent()

    private val validator = OutputValidator(schema = carSchema)


    @Test
    fun `successful validation 1`() {
        assertTrue(validator.isValid(valid1))
    }

    @Test
    fun `successful validation 2`() {
        assertTrue(validator.isValid(valid2))
    }

    @Test
    fun `failed validation 1`() {
        assertFalse(validator.isValid(invalid1))
    }

    @Test
    fun `failed validation 2`() {
        assertFalse(validator.isValid(invalid2))
    }

    @Test
    fun `validation messages fail 1`() {
        val messages: List<ValidationMessage> = validator.getValidationMessages(invalid1)
        assertEquals(1, messages.size)
        assertEquals("\$.vehicle_id: may only be 100 characters long", messages.toList()[0].message)
    }
}