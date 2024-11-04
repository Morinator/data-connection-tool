package com.digitalfrontiers.dataconnectiontool.validation

import com.networknt.schema.ValidationMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OutputValidationServiceTest {

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

    @Test
    fun `successful validation 1`() {
        assertTrue(OutputValidationService.isValid(valid1, carSchema))
    }

    @Test
    fun `successful validation 2`() {
        assertTrue(OutputValidationService.isValid(valid2, carSchema))
    }

    @Test
    fun `failed validation 1`() {
        assertFalse(OutputValidationService.isValid(invalid1, carSchema))
    }

    @Test
    fun `failed validation 2`() {
        assertFalse(OutputValidationService.isValid(invalid2, carSchema))
    }

    @Test
    fun `validation messages fail 1`() {
        val messages: List<ValidationMessage> = OutputValidationService.getValidationMessages(invalid1, carSchema)
        assertEquals(1, messages.size)
        assertEquals("\$.vehicle_id: may only be 100 characters long", messages.toList()[0].message)
    }
}