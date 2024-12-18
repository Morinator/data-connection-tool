package com.digitalfrontiers.transform

import com.jayway.jsonpath.PathNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestsOnData {

    private val sights: Data = listOf(
        mapOf(
            "name" to "Eiffel Tower",
            "height" to 300
        ),
        mapOf(
            "name" to "Statue of Liberty",
            "height" to 93
        ),
        mapOf(
            "name" to "Big Ben",
            "height" to 96
        )
    )

    private val studentGrades: Data = mapOf(
        "michael" to 3,
        "hillary" to 5,
        "josh" to 1
    )

    @Test
    fun `copy with no change`() {

        // given
        val spec = Input("$")

        // when
        val result = applyTransform(sights, spec)

        // then
        println(result)
        assertEquals(sights, result)
    }

    @Test
    fun `get list of entries by field-name`() {

        // given
        val spec = Record {
            "heights" from "$[*].height"
        }

        // when
        val result = applyTransform(sights, spec)

        // then
        val expected = mapOf(
            "heights" to listOf(300, 93, 96),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `get single entry`() {

        // given
        val spec = Input("$[0].name")

        // when
        val result = applyTransform(sights, spec)

        // then
        val expected = "Eiffel Tower"
        assertEquals(expected, result)
    }

    @Test
    fun `get first 2 names`() {

        // given
        val spec = Compose {
            Input("\$[0:2]") then
            ListOf {
                Record {
                    "name" from "name"
                }
            }
        }

        // when
        val result = applyTransform(sights, spec)

        // then
        val expected = listOf(
            mapOf("name" to "Eiffel Tower"),
            mapOf("name" to "Statue of Liberty"),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `error on invalid path`() {

        // given
        val spec = Input("$[0].qewqrwettcnbvcn")

        // when & then
        assertThrows<PathNotFoundException> {
            applyTransform(sights, spec)
        }
    }

    /**
     * This test checks that "ForEach" only works on list, and returns an empty list per default on all other types.
     */
    @Test
    fun `ListOf on non-list returns empty list`() {

        // given
        val spec = ListOf(
            Input("josh"), // no effect
        )

        // when
        val result = applyTransform(studentGrades, spec)

        // then
        val expected = listOf(1)
        assertEquals(expected, result)
    }

}