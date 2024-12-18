package com.digitalfrontiers.persistence

import com.digitalfrontiers.Mapping
import com.digitalfrontiers.transform.Self
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

@Entity
data class Entry(

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    val id: Long = 0,

    val data: Mapping,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    protected constructor(): this(0, Mapping("", "", Self), LocalDateTime.now())
}

@Converter(autoApply = true)
class MappingConverter(
    private val objectMapper: ObjectMapper
) : AttributeConverter<Mapping, String> {

    override fun convertToDatabaseColumn(attribute: Mapping): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(data: String): Mapping {
        return objectMapper.readValue(data, Mapping::class.java)
    }
}

interface MappingRepository: CrudRepository<Entry, Long>