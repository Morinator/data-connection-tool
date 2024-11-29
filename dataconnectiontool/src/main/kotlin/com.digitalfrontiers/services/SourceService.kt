package com.digitalfrontiers.services

import com.digitalfrontiers.components.Source
import org.springframework.stereotype.Service

@Service
class SourceService(
    private val sources: List<Source>
) {
    fun fetch(sourceId: String): Map<String, String> {
        return sources.firstOrNull {it.id == sourceId} ?.fetch()
            ?: throw IllegalArgumentException("Unknown source: $sourceId")
    }
}