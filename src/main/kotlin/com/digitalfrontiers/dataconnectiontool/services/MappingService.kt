package com.digitalfrontiers.dataconnectiontool.services

import com.digitalfrontiers.datatransformlang.transform.Record
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sources: SourceService,
    private val transforms: TransformService,
    private val sinks: SinkService
) {
    fun transfer(sourceId: String, sinkId: String) {
        val transform = transforms.createTransform(
            Record {
                "x" from "a"
                "y" from "b"
            }
        )

        // TODO: Validation
        val data = sources.fetch(sourceId)
        val transformed = transform.apply(data) as Map<String, String>

        sinks.put(sinkId, transformed)
    }
}