package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.components.Sink
import org.springframework.stereotype.Service

@Service
class SinkService(
    private val sinks: List<Sink>,
) {

    /**
     * @throws [IllegalArgumentException] if no sink exist for [sinkId]
     */
    fun put(sinkId: String, data: List<Map<String, String>>) {
        sinks.firstOrNull { it.id == sinkId }?.put(data)
            ?: throw IllegalArgumentException("Unknown sink: $sinkId")
    }

    fun getFormat(sinkId: String): Format =
        sinks.firstOrNull { it.id == sinkId }?.format
            ?: throw IllegalArgumentException("Unknown sink: $sinkId")
}