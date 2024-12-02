package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.transform.Record
import com.digitalfrontiers.util.parseTransformNode
import com.fasterxml.jackson.databind.JsonNode
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

    fun validate(sourceId: String, sinkId: String, spec: JsonNode): Boolean {
        if (parseTransformNode(spec) !is Record) {
            return false // we only allow transformations via flat Record objects
        }

        return validateSource() && validateSink(sinkId, parseTransformNode(spec) as Record)
    }

    fun validateSource(): Boolean {
        return true
    }

    /**
     * Each mandatory field of the sink must be covered
     * Each field of the transformation must be used in the sink, either in mandatory or optional field
     */
    fun validateSink(sinkId: String, record: Record): Boolean {
        val recordKeys : List<String> = record.entries.keys.toList()
        val sinkFormat: Format = sinkService.getFormat(sinkId)

        val allSinkFieldsAreCovered =  recordKeys.containsAll(sinkFormat.mandatoryFields)
        val allRecordKeysAreUsed : Boolean = (sinkFormat.getAllFields()).containsAll(recordKeys)

        return allSinkFieldsAreCovered && allRecordKeysAreUsed
    }

}