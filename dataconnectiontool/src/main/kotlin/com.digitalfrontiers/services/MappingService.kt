package com.digitalfrontiers.dataconnectiontool.services

import com.digitalfrontiers.datatransformlang.transform.Specification
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sources: SourceService,
    private val transforms: TransformService,
    private val sinks: SinkService
) {
    fun map(sourceId: String, sinkId: String, spec: Specification) {
        val transform = transforms.createTransform(
            spec
        )

        // TODO: Validation
        val data = sources.fetch(sourceId)
        val transformed = transform.apply(data) as Map<String, String>

        sinks.put(sinkId, transformed)
    }
}