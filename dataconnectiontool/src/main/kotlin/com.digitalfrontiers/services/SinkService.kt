package com.digitalfrontiers.dataconnectiontool.services

import com.digitalfrontiers.dataconnectiontool.components.ISink
import org.springframework.stereotype.Service

@Service
class SinkService(
    private val sinks: List<ISink>
) {
    fun put(sinkId: String, data: Map<String, String>) {
        sinks.firstOrNull {it.id == sinkId} ?.put(data)
            ?: throw IllegalArgumentException("Unknown sink: $sinkId")
    }
}