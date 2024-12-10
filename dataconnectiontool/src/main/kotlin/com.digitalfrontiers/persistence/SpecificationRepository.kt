package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.util.parseTransformConfig
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.time.LocalDateTime
import javax.sql.DataSource


/**
 * Stores instances of [Specification]
 */
class SpecificationRepository(databaseID: String = "") {

    data class SpecificationEntry(
        val id: Long? = null,
        val data: Specification,
        val createdAt: LocalDateTime = LocalDateTime.now()
    )

    private val dataSource: DataSource = DriverManagerDataSource().apply {
        setDriverClassName("org.h2.Driver")
        url = "jdbc:h2:mem:nestdb${databaseID};DB_CLOSE_DELAY=-1"
        username = "sa"
        password = ""
    }

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource)
        .withTableName("table1")
        .usingGeneratedKeyColumns("id")

    private val rowMapper = RowMapper { rs, _ ->
        val data: Specification = parseTransformConfig(rs.getString("data"))

        SpecificationEntry(
            id = rs.getLong("id"),
            data = data,
            createdAt = rs.getObject("created_at", LocalDateTime::class.java)
        )
    }

    private val objectMapper = SpecificationJsonConfig.createMapper()

    init {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS table1 (
                id LONG AUTO_INCREMENT PRIMARY KEY,
                data TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            )
        """)
    }

    fun save(data: Specification): Long {
        val parameters = mapOf(
            "data" to objectMapper.writeValueAsString(data),
            "created_at" to LocalDateTime.now()
        )
        val id = jdbcInsert.executeAndReturnKey(parameters).toLong()
        return id
    }

    fun getById(id: Long): SpecificationEntry? =
        jdbcTemplate.query(
            "SELECT * FROM table1 WHERE id = ?",
            rowMapper,
            id
        ).firstOrNull()

    fun getAllRows(): List<SpecificationEntry> =
        jdbcTemplate.query(
            "SELECT * FROM table1 ORDER BY created_at DESC",
            rowMapper
        )

    /**
     * Removes all entries from the table
     */
    fun clearAll() {
        jdbcTemplate.execute("DELETE FROM table1")
    }
}