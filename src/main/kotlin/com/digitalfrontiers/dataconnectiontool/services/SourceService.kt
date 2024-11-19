package com.digitalfrontiers.dataconnectiontool.services

import com.digitalfrontiers.dataconnectiontool.components.ISource
import org.springframework.stereotype.Service

@Service
class SourceService(
    private val sources: List<ISource>
) {
    fun fetch(sourceId: String): Map<String, String> {
        return sources.firstOrNull {it.id == sourceId} ?.fetch()
            ?: throw IllegalArgumentException("Unknown source: $sourceId")
    }
}