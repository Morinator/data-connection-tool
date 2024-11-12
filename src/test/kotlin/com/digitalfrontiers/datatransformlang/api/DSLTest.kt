package com.digitalfrontiers.datatransformlang.api

import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DSLTest {

    @Test
    fun extensionTest() {
        val data = mapOf(
            "a" to 1
        )

        val expected = mapOf(
            "a" to 1,
            "b" to 2,
            "c" to 3
        )

        val transform = Transform to {
            Self extendedWith {
                "b" to 2
                "c" to 3
            }
        }

        val result = transform.apply(data)

        assertEquals(expected, result)
    }

    @Test
    fun remapTest() {
        val data = mapOf(
            "a" to 1,
            "b" to 2,
            "c" to 3
        )

        val transformXYZ = Transform to {
            Self remapping {
                "a" to "x"
                "b" to "y"
                "c" to "z"
            }
        }

        val firstResult = transformXYZ.apply(data)

        assertEquals(mapOf(
            "x" to 1,
            "y" to 2,
            "z" to 3
        ), firstResult)

        val transformUpper = Transform to {
            Self remappedWith {
                it.uppercase()
            }
        }

        val secondResult = transformUpper.apply(data)

        assertEquals(mapOf(
            "A" to 1,
            "B" to 2,
            "C" to 3
        ), secondResult)
    }
}