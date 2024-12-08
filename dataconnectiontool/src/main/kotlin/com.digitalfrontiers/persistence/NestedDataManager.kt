package com.digitalfrontiers.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
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
        .withTableName("nested_data")
        .usingGeneratedKeyColumns("id")

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
    }

    private val nestedDataRowMapper = RowMapper { rs, _ ->
        val nestedData: List<List<Map<String, Any>>> = objectMapper.readValue(rs.getString("data"))

        NestedData(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            data = nestedData,
            createdAt = rs.getObject("created_at", LocalDateTime::class.java)
        )
    }

    fun createTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS nested_data (
                id LONG AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                data TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            )
        """)
        println("Table created successfully!")
    }

    fun saveNestedData(nestedData: NestedData): Long {
        val parameters = mapOf(
            "name" to nestedData.name,
            "data" to objectMapper.writeValueAsString(nestedData.data),
            "created_at" to nestedData.createdAt
        )

        val id = jdbcInsert.executeAndReturnKey(parameters).toLong()
        println("Saved nested data with ID: $id")
        return id
    }

    fun getNestedDataById(id: Long): NestedData? =
        jdbcTemplate.query(
            "SELECT * FROM nested_data WHERE id = ?",
            nestedDataRowMapper,
            id
        ).firstOrNull()

    fun getAllNestedData(): List<NestedData> =
        jdbcTemplate.query(
            "SELECT * FROM nested_data ORDER BY created_at DESC",
            nestedDataRowMapper
        )
}