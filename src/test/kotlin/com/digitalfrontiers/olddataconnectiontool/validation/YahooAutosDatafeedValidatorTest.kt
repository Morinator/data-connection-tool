package com.digitalfrontiers.olddataconnectiontool.validation

import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class YahooAutosDatafeedValidatorTest {

    private lateinit var car1: MutableMap<Any, Any>

    private val validator = YahooAutosDatafeedValidator()

    // resets before each unit test
    @BeforeEach
    fun setup() {
        // not real data
        car1 = mutableMapOf(
            "seq_no" to 1002,
            "brand" to "Huawei",
            "model" to "Macan",
            "year" to "2020",
            "month" to "09",
            "displacement" to 2500,
            "price" to 123456,
            "area" to "台北市",
            "area_show" to "台北保時捷中心",
            "desc" to "台北保時捷中心 原廠認證中古車",
            "img_url" to "https://example.com/photo/2.jpg",
            "link_url" to "https://example.com/listing/1002/macan",
            "gear" to "AT",
            "color" to "白色",
            "update_time" to "2024-06-22 18:19:20",
            "certificate" to "Y",
            "show_status" to "Y"
        )

    }

    @Test
    fun `test valid input`() {

        println(validator.getValidationMessages(car1))
        kotlin.test.assertEquals(0, validator.getValidationMessages(car1).size)
        assertTrue(validator.isValid(car1))
    }

    @Test
    fun `missing field`() {
        car1.remove("seq_no")
        kotlin.test.assertEquals(
            "\$.seq_no: is missing but it is required",
            validator.getValidationMessages(car1)[0].message
        )
    }

    @Test
    fun `invalid gear`() {
        car1["gear"] = "amt" // has to be upper case
        kotlin.test.assertEquals(
            "\$.gear: does not have a value in the enumeration [AT, MT, AMT]",
            validator.getValidationMessages(car1)[0].message
        )
    }


}