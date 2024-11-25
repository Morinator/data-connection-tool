package com.digitalfrontiers.services

import com.digitalfrontiers.components.ISource
import org.springframework.stereotype.Service

@Service
class SourceService(
    private val sources: List<ISource>
) {

    fun hasData(sourceId: String): Boolean {
        return sources.firstOrNull {it.id == sourceId} ?.hasData()
            ?: throw IllegalArgumentException("Unknown source: $sourceId")
    }

    fun fetch(sourceId: String): Map<String, String> {
        return sources.firstOrNull {it.id == sourceId} ?.fetch()
            ?: throw IllegalArgumentException("Unknown source: $sourceId")
    }
}