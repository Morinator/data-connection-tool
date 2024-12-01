package com.digitalfrontiers.services

import com.digitalfrontiers.transform.Specification
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sources: SourceService,
    private val transforms: TransformService,
    private val sinks: SinkService,
) {

    /**
     * Runs the mapping specified by [spec] from [sourceId] to [sinkId].
     *
     * // TODO Validation
     * // TODO avoid casting ??
     */
    fun map(sourceId: String, sinkId: String, spec: Specification) {
        val transform = transforms.createTransform(spec)
        val data = sources.fetch(sourceId)
        val transformed = transform.apply(data) as Map<String, String>
        sinks.put(sinkId, transformed)
    }
}