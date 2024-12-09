package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Specification
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sourceService: SourceService,
    private val transformService: TransformService,
    private val sinkService: SinkService,
) {

    /**
     * Runs the mapping specified by [spec] from [sourceId] to [sinkId].
     */
    fun map(sourceId: String, sinkId: String, spec: Specification.Record) {
        val transform = transformService.createTransform(spec)
        val data = sourceService.fetch(sourceId)
        val transformed = transform.apply(data) as List<Map<String, String>>
        sinkService.put(sinkId, transformed)
    }

    fun validate(sourceId: String, sinkId: String, spec: Specification): Boolean {

        // Spec must be a flat Record
        val record: Specification.Record = spec as? Specification.Record ?: return false
        val sinkFormat: Format = sinkService.getFormat(sinkId)
        val sourceFormat: Format = sourceService.getFormat(sourceId)

        // 1. Basic field coverage checks
        val allRequiredSinkFieldsCovered = sinkFormat.requiredFields.all { it in record.entries.keys }
        val allRecordKeysUsed = record.entries.keys.all { it in sinkFormat.getAllFields() }

        if (!allRequiredSinkFieldsCovered || !allRecordKeysUsed) {
            return false
        }

        // 2. Get all Input specifications from the record
        val inputs = getAllInputs(record)

        // 3. Check field dependency rules

        // 3a. Required sink fields must only depend on required source fields
        val requiredSinkFieldsValid = sinkFormat.requiredFields.all { sinkField ->
            val input = inputs[sinkField] ?: return@all false
            val sourceField = input.path
            sourceField in sourceFormat.requiredFields
        }

        if (!requiredSinkFieldsValid) {
            return false
        }

        // 3b. Optional sink fields must only depend on optional source fields
        val optionalSinkFieldsValid = sinkFormat.optionalFields.all { sinkField ->
            val input = inputs[sinkField] ?: return@all true // Optional fields don't need to be mapped
            val sourceField = input.path
            sourceField in sourceFormat.getAllFields()
        }

        if (!optionalSinkFieldsValid) {
            return false
        }

        return true
    }

    /**
     * Since spec is flat and only has a Record at the root,
     * we can directly filter for entries that are Input specs.
     */
    private fun getAllInputs(record: Specification.Record): Map<String, Input> {
        return record.entries
            .filterValues { it is Input }
            .mapValues { (_, spec) -> spec as Input }
    }

}