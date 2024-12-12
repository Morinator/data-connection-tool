package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Transformation
import com.digitalfrontiers.services.JsonService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


/**
 * Stores instances of [Transformation]
 */
@Repository
class TransformationRepository(
    private val jdbcTemplate: JdbcTemplate,
    val jsonService: JsonService
) {

    data class Entry(
        val id: Long,
        val data: Transformation,
        val createdAt: LocalDateTime = LocalDateTime.now()
    )

    private val jdbcInsert = SimpleJdbcInsert(jdbcTemplate)
        .withTableName("table1")
        .usingGeneratedKeyColumns("id")

    private val rowMapper = RowMapper { rs, _ ->
        val data: Transformation = jsonService.stringToTransformation(rs.getString("data"))

        Entry(
            id = rs.getLong("id"),
            data = data,
            createdAt = rs.getObject("created_at", LocalDateTime::class.java)
        )
    }

    fun save(data: Transformation): Long {
        val parameters = mapOf(
            "data" to jsonService.transformationToJson(data),
            "created_at" to LocalDateTime.now()
        )
        val id = jdbcInsert.executeAndReturnKey(parameters).toLong()
        return id
    }

    fun getById(id: Long): Entry? =
        jdbcTemplate.query(
            "SELECT * FROM table1 WHERE id = ?",
            rowMapper,
            id
        ).firstOrNull()

    fun getAllRows(): List<Entry> =
        jdbcTemplate.query(
            "SELECT * FROM table1 ORDER BY created_at DESC",
            rowMapper
        )

    /**
     * Deletes an entry by its ID
     * @param id The ID of the entry to delete
     * @return true if an entry was deleted, false if no entry with the given ID existed
     */
    fun deleteById(id: Long): Boolean {
        val rowsAffected = jdbcTemplate.update("DELETE FROM table1 WHERE id = ?", id)
        return rowsAffected > 0
    }

    fun update(id: Long, data: Transformation): Boolean {
        val rowsAffected = jdbcTemplate.update(
            """UPDATE table1 SET data = ?, created_at = ? WHERE id = ?""",
            jsonService.transformationToJson(data),
            LocalDateTime.now(),
            id
        )
        return rowsAffected > 0
    }
}