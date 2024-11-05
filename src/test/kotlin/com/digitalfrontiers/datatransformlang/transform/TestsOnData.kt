package com.digitalfrontiers.datatransformlang.transform

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
    fun `get list of entries by field-name`() {

        // given
        val spec = ToObject(
            "heights" to ToInput("$[*].height"),
        )

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
        val spec = ToInput("$[0].name")

        // when
        val result = applyTransform(sights, spec)

        // then
        val expected = "Eiffel Tower"
        assertEquals(expected, result)
    }

    @Test
    fun `get first 2 names`() {

        // given
        val spec = Compose(
            ToInput("\$[0:2]"),
            ForEach(
                ToObject(
                    "name" to ToInput("name")
                )
            )
        )

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
        val spec = ToInput("$[0].qewqrwettcnbvcn")

        // when & then
        assertThrows<PathNotFoundException> {
            applyTransform(sights, spec)
        }
    }

    /**
     * This test checks that "ForEach" only works on list, and returns an empty list per default on all other types.
     */
    @Test
    fun `foreach on non-list returns empty list`() {

        // given
        val spec = ForEach(
            ToInput("josh"), // no effect
        )

        // when
        val result = applyTransform(studentGrades, spec)

        // then
        val expected = emptyList<Data>()
        assertEquals(expected, result)
    }

}