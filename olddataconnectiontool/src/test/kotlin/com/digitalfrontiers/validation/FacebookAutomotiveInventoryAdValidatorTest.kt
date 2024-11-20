package com.digitalfrontiers.validation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FacebookAutomotiveInventoryAdValidatorTest {


    private lateinit var car1: MutableMap<String, Any>

    private val validator = FacebookAutomotiveInventoryAdValidator()

    // resets before each unit test
    @BeforeEach
    fun setup() {
        // not real data
        car1 = mutableMapOf(
            "body_style" to "CONVERTIBLE",
            "description" to "Porsche 718 Boxster, Porsche Approved Gebrauchtwagen",
            "exterior_color" to "Grau",
            "interior_color" to "Serienausstattung Schwarz",
            "make" to "Porsche",
            "model" to "911",
            "state_of_vehicle" to "CPO",
            "title" to "Porsche Macan S",
            "url" to "https://www.digitalfrontiers.de",
            "vehicle_id" to "1234",
            "vin" to "A1B2C3D4E5F6",
            "year" to 2024,
            "condition" to "GOOD",
            "drivetrain" to "AWD",
            "fuel_type" to "HYBRID",
            "transmission" to "Automatic",
            "trim" to "911 GT3",
            "price" to "12345 EUR",
            "latitude" to 50.813806,
            "longitude" to 8.744864,
            "dealer_name" to "Porsche Zentrum Böblingen",
            "custom_label_0" to 2022,
            "mileage" to mapOf(
                "unit" to "KM",
                "value" to 9876,
            )
        )
    }

    @Test
    fun `test valid input`() {

        println(validator.getValidationMessages(car1))
        assertEquals(0, validator.getValidationMessages(car1).size)
        assertTrue(validator.isValid(car1))
    }

    @Test
    fun `missing field`() {
        car1.remove("body_style")
        assertEquals(
            "\$.body_style: is missing but it is required",
            validator.getValidationMessages(car1)[0].message
        )
    }

    @Test
    fun `invalid drivetrain`() {
        car1["drivetrain"] = "Rentierschlitten"
        assertEquals(
            "\$.drivetrain: does not have a value in the enumeration [4X2, 4X4, AWD, FWD, RWD, Other]",
            validator.getValidationMessages(car1)[0].message
        )
    }
}