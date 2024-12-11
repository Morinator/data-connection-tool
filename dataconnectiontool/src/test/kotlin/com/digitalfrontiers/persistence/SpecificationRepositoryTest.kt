package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class SpecificationRepositoryTest {

    @Autowired
    lateinit var repo: SpecificationRepository

    private val someRecord = Specification.Record {
        "x" from "a"
        "y" from "b"
    }

    @Test
    fun `save and retrieve simple record`() {
        repo.save(someRecord)
        val entry = repo.getById(1)!!

        assertEquals(1, entry.id)
        assertEquals(
            Specification.Record {
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

    @Disabled
    @Test
    fun `id is automatically incremented `() {
        assertEquals(1, repo.save(someRecord))
        assertEquals(2, repo.save(someRecord))
        assertEquals(listOf(2L, 1L), repo.getAllRows().map { it.id })
    }
}