package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

class SpecificationRepositoryTest {

    private lateinit var repo: SpecificationRepository

    @BeforeEach // creates database with unique ID each time to avoid side effects
    fun setup() {
        val uuid: String = UUID.randomUUID().toString()
        repo = SpecificationRepository(databaseID = uuid)

    }

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
        createTableWithEntry(repo)
        val entry = repo.getById(1)!!

        assertEquals(1, entry.id)
        assertEquals(
            Specification.Record{
                "x" from "a"
                "y" from "b"
            },
            entry.data
        )

        println("should be only one entry:")
        repo.getAllRows().forEach { println(it) }
    }

    @Test
    fun `missing entry results in null`() {
        assertNull(repo.getById(1))
    }

    @Test
    fun `id is automatically incremented `() {
        createTableWithEntry(repo)
        createTableWithEntry(repo)
        assertEquals(listOf(2L, 1L), repo.getAllRows().map { it.id })
    }
}