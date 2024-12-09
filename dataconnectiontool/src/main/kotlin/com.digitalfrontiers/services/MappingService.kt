package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.transform.Specification.Input
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
    fun map(sourceId: String, sinkId: String, spec: Record) {
        val transform = transformService.createTransform(spec)
        val data = sourceService.fetch(sourceId)
        val transformed = transform.apply(data) as List<Map<String, String>>
        sinkService.put(sinkId, transformed)
    }

    fun validate(sourceId: String, sinkId: String, spec: Specification): Boolean {
        // Spec must be a flat Record
        val record = spec as? Record ?: return false
        val sinkFormat: Format = sinkService.getFormat(sinkId)
        val sourceFormat: Format = sourceService.getFormat(sourceId)

        // 1. Basic field coverage checks
        val allRequiredSinkFieldsCovered = sinkFormat.requiredFields.all { it in record.entries.keys }
        val allRecordKeysUsed = record.entries.keys.all { it in sinkFormat.getAllFields() }

        if (!allRequiredSinkFieldsCovered || !allRecordKeysUsed) {
            return false
        }

        return true
    }

    /**
     * Since spec is flat and only has a Record at the root,
     * we can directly filter for entries that are Input specs.
     */
    private fun getAllInputs(record: Record): Map<String, Input> {
        return record.entries
            .filterValues { it is Input }
            .mapValues { (_, spec) -> spec as Input }
    }

}