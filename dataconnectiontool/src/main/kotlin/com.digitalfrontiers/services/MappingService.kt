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

        val record: Specification.Record = spec as? Specification.Record ?: return false
        val sinkFormat: Format = sinkService.getFormat(sinkId)
        val sourceFormat: Format = sourceService.getFormat(sourceId)

        // create a map containing all relevant items of type [Specification.Input]
        val inputElements = record.entries
            .filterValues { it is Input }
            .mapValues { (_, spec) -> spec as Input }

        // ########## VALIDATION RULES ##########
        // ######################################

        // Basic field coverage checks
        val allRequiredSinkFieldsCovered = sinkFormat.requiredFields.all { it in record.entries.keys }
        val allRecordKeysUsed = record.entries.keys.all { it in sinkFormat.getAllFields() }


        // 3a. Required sink fields must only depend on required source fields
        val requiredSinkFieldsValid = sinkFormat.requiredFields
            .filter { it in inputElements.keys }
            .all {inputElements.getValue(it).path in sourceFormat.requiredFields }

        // 3b. Optional sink fields may depend on any source field
        val optionalSinkFieldsValid = sinkFormat.optionalFields
            .filter { it in inputElements.keys }
            .all { inputElements.getValue(it).path in sourceFormat.getAllFields()}

        return allRequiredSinkFieldsCovered && allRecordKeysUsed && requiredSinkFieldsValid && optionalSinkFieldsValid
    }

}