package com.digitalfrontiers.dataconnectiontool.services

import com.digitalfrontiers.dataconnectiontool.components.ISink
import com.digitalfrontiers.dataconnectiontool.components.ISource
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sources: List<ISource>,
    private val sinks: List<ISink>
) {
    fun transfer(sourceType: String, sinkType: String) {
        val data = sources.firstOrNull {it.id == sourceType} ?.process()
            ?: throw IllegalArgumentException("Unknown source: $sourceType")

        sinks.firstOrNull {it.id == sinkType} ?.process(data)
            ?: throw IllegalArgumentException("Unknown sink: $sinkType")
    }
}