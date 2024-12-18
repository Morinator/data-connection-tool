package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.components.Source
import org.springframework.stereotype.Service

@Service
class SourceService(
    private val sources: List<Source>,
) {

    private fun getSource(sourceId: String): Source =
        requireNotNull(sources.firstOrNull { it.id == sourceId }) {
            throw SourceNotFoundException(sourceId)
        }

    /**
     * @throws [SourceNotFoundException] if no sources exist for [sourceId]
     */
    fun fetch(sourceId: String): List<Map<String, String>> = getSource(sourceId).fetch()

    /**
     * @throws [SourceNotFoundException] if no sources exist for [sourceId]
     */
    fun getFormat(sourceId: String): Format = getSource(sourceId).format
}

class SourceNotFoundException(sourceId: String) : RuntimeException("Unknown source: $sourceId")