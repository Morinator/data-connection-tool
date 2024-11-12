package com.digitalfrontiers.datatransformlang.api

import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DSLTest {

    @Test
    fun extensionTest() {
        val json = """
            {
                "a": 1
            }
        """.trimIndent()

        val expected = """
            {"a":1,"b":2,"c":3}
        """.trimIndent()

        val transform = Transform to {
            Self extendedWith {
                "b" to 2
                "c" to 3
            }
        }

        val t = Object {
            "givenName" from "$.firstName"
            "a" from "$.a"
        }

        val result = transform.apply(json)

        assertEquals(expected, result)
    }
}