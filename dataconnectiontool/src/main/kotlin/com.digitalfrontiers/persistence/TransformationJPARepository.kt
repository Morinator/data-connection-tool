package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Self
import com.digitalfrontiers.transform.Transformation
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Entity
data class Entry(

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    val id: Long = 0,

    val data: Transformation,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    protected constructor(): this(0, Self, LocalDateTime.now())
}

@Component
@Converter(autoApply = true)
class TransformationConverter(
    private val objectMapper: ObjectMapper
) : AttributeConverter<Transformation, String> {

    override fun convertToDatabaseColumn(attribute: Transformation): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(data: String): Transformation {
        return objectMapper.readValue(data, Transformation::class.java)
    }
}

interface TransformationJPARepository: CrudRepository<Entry, Long>

fun TransformationJPARepository.createEntry(transformation: Transformation): Long = this.save(Entry(data = transformation)).id

fun TransformationJPARepository.updateEntry(id: Long, transformation: Transformation): Boolean {
    val entry = this.findById(id).orElse(null)

    return if (entry != null) {
        val updatedEntry = entry.copy(data = transformation) // TODO: Add updatedAt to Schema?
        this.save(updatedEntry)
        true
    } else {
        false
    }
}

fun TransformationJPARepository.deleteEntry(id: Long): Boolean {
    val entry = this.findById(id).orElse(null)

    return if (entry != null) {
        this.deleteById(id)
        true
    } else {
        false
    }
}