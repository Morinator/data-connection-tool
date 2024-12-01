package com.digitalfrontiers.services

import com.digitalfrontiers.components.Source
import org.springframework.stereotype.Service

@Service
class SourceService(
    private val sources: List<Source>,
) {

    /**
     * @throws [IllegalArgumentException] if no sources exist for [sourceId]
     */
    fun fetch(sourceId: String): List<Map<String, String>> {
        return sources.firstOrNull { it.id == sourceId }?.fetch()
            ?: throw IllegalArgumentException("Unknown source: $sourceId")
    }
}