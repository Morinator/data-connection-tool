package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class SpecificationRepositoryTest {

    private val manager = SpecificationRepository()

    private fun createTableWithEntry(manager: SpecificationRepository) {

        manager.save(
            SpecificationRepository.SpecificationEntry(
                data = Specification.Record {
                    "x" from "a"
                    "y" from "b"
                }
            )
        )
    }

    @Test
    fun `save and retrieve simple record`() {
        createTableWithEntry(manager)
        val entry = manager.getById(1)!!

        assertEquals(1, entry.id)
        assertEquals(
            Specification.Record{
                "x" from "a"
                "y" from "b"
            },
            entry.data
        )

        println("should be only one entry:")
        manager.getAllRows().forEach { println(it) }
    }

    @Test
    fun `missing entry results in null`() {
        assertNull(manager.getById(1))
    }

    @Test
    fun `id is automatically incremented `() {
        createTableWithEntry(manager)
        createTableWithEntry(manager)

        assertEquals(listOf(2L, 1L), manager.getAllRows().map { it.id })
    }

}