package com.digitalfrontiers.services

import com.digitalfrontiers.Mapping
import com.digitalfrontiers.components.Format
import com.digitalfrontiers.persistence.Entry
import com.digitalfrontiers.persistence.MappingRepository
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.transform.Transformation
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MappingService(
    private val mappingRepository: MappingRepository,
    private val sourceService: SourceService,
    private val transformService: TransformService,
    private val sinkService: SinkService,
) {

    private fun getEntryOrRaiseException(id: Long): Entry =
        requireNotNull(this.mappingRepository.findById(id).getOrNull()) {
            throw MappingNotFoundException("Mapping with ID $id not found")
        }

    fun save(mapping: Mapping): Long {
//        validate(mapping)

        return this.mappingRepository.save(Entry(data = mapping)).id
    }

    // TODO: Return JSON/Map with IDs as keys, instead?
    fun getAll(): List<Mapping> = this.mappingRepository.findAll().toList().map { it.data }

    fun getById(id: Long): Mapping = getEntryOrRaiseException(id).data

    fun update(id: Long, mapping: Mapping) {
        val updatedEntry = getEntryOrRaiseException(id).copy(data = mapping) // TODO: Add updatedAt to Schema?

        this.mappingRepository.save(updatedEntry)
    }

    fun delete(id: Long) {
        // TODO: Check for existence and throw error?
        val entry = this.mappingRepository.findById(id).orElse(null)

        if (entry != null) {
            this.mappingRepository.deleteById(id)
        }
    }

    fun invoke(id: Long) {
        val mapping = getEntryOrRaiseException(id).data

        val transform = transformService.createTransform(mapping.transformation as Record)
        val data = sourceService.fetch(mapping.sourceId)
        val transformedData = transform.apply(data) as List<Map<String, String>>
        sinkService.put(mapping.sinkId, transformedData)
    }

    fun validate(mapping: Mapping) {

        val record: Transformation.Record = mapping.transformation as Transformation.Record
        val sourceFormat: Format = sourceService.getFormat(mapping.sourceId)
        val sinkFormat: Format = sinkService.getFormat(mapping.sinkId)

        val inputElements = record.entries
            .filterValues { it is Input }
            .mapValues { (_, spec) -> spec as Input }

        // ########## VALIDATION RULES ##########
        // ######################################

        // Basic field coverage checks
        val allRequiredSinkFieldsCovered = sinkFormat.requiredFields.all { it in record.entries.keys }
        val allRecordKeysUsed = record.entries.keys.all { it in sinkFormat.getAllFields() }


        // Required sink fields must only depend on required source fields
        val requiredSinkFieldsValid = sinkFormat.requiredFields
            .filter { it in inputElements.keys }
            .all {inputElements.getValue(it).path in sourceFormat.requiredFields }

        // Optional sink fields may depend on any source field
        val optionalSinkFieldsValid = sinkFormat.optionalFields
            .filter { it in inputElements.keys }
            .all { inputElements.getValue(it).path in sourceFormat.getAllFields()}

        check(allRequiredSinkFieldsCovered) { throw InvalidMappingException("Transformation is missing some of the fields required in sink format") }
        check(allRecordKeysUsed) { throw InvalidMappingException("Transformation defines some fields not specified in sink format") }
        check(requiredSinkFieldsValid) { throw InvalidMappingException("Fields required in sink format must only depend on fields also required in source format") }
        check(optionalSinkFieldsValid) { throw InvalidMappingException("Fields defined in transformation must be part of source format") }

        // TODO: Return boolean, nonetheless?
    }
}

class MappingNotFoundException(m: String): RuntimeException(m)

class InvalidMappingException(m: String): RuntimeException(m)