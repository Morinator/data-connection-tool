package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.util.parseTransformConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.time.LocalDateTime
import javax.sql.DataSource

class NestedDataManager {
    private val dataSource: DataSource = DriverManagerDataSource().apply {
        setDriverClassName("org.h2.Driver")
        url = "jdbc:h2:./nesteddb;DB_CLOSE_DELAY=-1"
        username = "sa"
        password = ""
    }

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource)
        .withTableName("table1")
        .usingGeneratedKeyColumns("id")

    private val objectMapper: ObjectMapper = ObjectMapper()

    private val rowMapper = RowMapper { rs, _ ->
        val data: Specification = parseTransformConfig(rs.getString("data"))

        NestedData(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            data = data,
            createdAt = rs.getObject("created_at", LocalDateTime::class.java)
        )
    }

    fun createTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS table1 (
                id LONG AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                data TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            )
        """)
        println("Table created successfully!")
    }

    fun saveNestedData(nestedData: NestedData): Long {
        val data : String  = objectMapper.writeValueAsString(nestedData.data)
        val parameters = mapOf(
            "name" to nestedData.name,
            "data" to data,
            "created_at" to nestedData.createdAt
        )

        val id = jdbcInsert.executeAndReturnKey(parameters).toLong()
        println("Saved nested data with ID: $id")
        return id
    }

    fun getNestedDataById(id: Long): NestedData? =
        jdbcTemplate.query(
            "SELECT * FROM table1 WHERE id = ?",
            rowMapper,
            id
        ).firstOrNull()

    fun allRows(): List<NestedData> =
        jdbcTemplate.query(
            "SELECT * FROM table1 ORDER BY created_at DESC",
            rowMapper
        )
}