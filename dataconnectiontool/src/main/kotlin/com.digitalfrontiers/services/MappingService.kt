package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Record
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
    fun map(sourceId: String, sinkId: String, spec: Record) {
        val transform = transformService.createTransform(spec)
        val data = sourceService.fetch(sourceId)
        val transformed = transform.apply(data) as List<Map<String, String>>
        sinkService.put(sinkId, transformed)
    }

    fun validate(sourceId: String, sinkId: String, spec: Specification): Boolean {
        val record: Record = spec as? Record ?: return false

        return validateSource(sourceId, record) && validateSink(sinkId, record)
    }

    /**
     * Every field used in the transformation has to be required in the source
     *
     */
    fun validateSource(sourceId: String, record: Record): Boolean {

        // assumes Record doesn't contain relevant nesting and only Input is relevant
        val usedFields = record
            .entries
            .values
            .filterIsInstance<Input>()
            .map { it.path }

        return sourceService
            .getFormat(sourceId)
            .requiredFields.containsAll(usedFields)
    }

    /**
     * Each required field of the sink must be covered
     * Each field of the transformation must be used in the sink, either in required or optional field
     */
    fun validateSink(sinkId: String, record: Record): Boolean {
        val recordKeys : List<String> = record.entries.keys.toList()
        val sinkFormat: Format = sinkService.getFormat(sinkId)

        val allSinkFieldsAreCovered =  recordKeys.containsAll(sinkFormat.requiredFields)
        val allRecordKeysAreUsed : Boolean = (sinkFormat.getAllFields()).containsAll(recordKeys)

        return allSinkFieldsAreCovered && allRecordKeysAreUsed
    }

}