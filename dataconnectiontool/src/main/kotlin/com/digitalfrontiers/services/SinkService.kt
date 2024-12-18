package com.digitalfrontiers.services

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.components.Sink
import org.springframework.stereotype.Service

@Service
class SinkService(
    private val sinks: List<Sink>,
) {
    private fun getSink(sinkId: String): Sink =
        requireNotNull(sinks.firstOrNull { it.id == sinkId }) {
            throw SinkNotFoundException(sinkId)
        }

    /**
     * @throws [SinkNotFoundException] if no sinks exist for [sinkId]
     */
    fun put(sinkId: String, data: List<Map<String, String>>) = getSink(sinkId).put(data)

    /**
     * @throws [SinkNotFoundException] if no sinks exist for [sinkId]
     */
    fun getFormat(sinkId: String): Format = getSink(sinkId).format
}

class SinkNotFoundException(sinkId: String) : RuntimeException("Unknown sink: $sinkId")