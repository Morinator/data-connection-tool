package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class TransformationDataManagerTest {

    private val manager = TransformationDataManager()

    @Test
    fun save() {
    }

    @Test
    fun getById() {
        createTableWithEntry(manager)
        val entry = manager.getById(1)!!

        assertEquals(1, entry.id)
        assertEquals(
            Specification.Record{
                "x" from "a"
                "y" from "b"
            },
            manager.getById(1)!!.data
        )
    }

    @Test
    fun getAllRows() {
    }
}