package com.digitalfrontiers.services

import com.digitalfrontiers.Transform
import com.digitalfrontiers.components.ICustomFunction
import com.digitalfrontiers.components.ISink
import com.digitalfrontiers.components.ISource
import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.with
import kotlinx.coroutines.*
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sourceProvider: ObjectProvider<ISource>,
    private val sinkProvider: ObjectProvider<ISink>,
    private val customFunctions: List<ICustomFunction> = emptyList()
) {

    private val mappingJobs: MutableMap<String, Job> = mutableMapOf()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private fun createSource(sourceId: String): ISource {
        return sourceProvider.stream()
            .filter { it.id == sourceId }
            .findFirst()
            .orElseThrow { IllegalArgumentException("Unknown source: $sourceId") }
    }

    private fun createSink(sinkId: String) : ISink {
        return sinkProvider.stream()
            .filter { it.id == sinkId }
            .findFirst()
            .orElseThrow { IllegalArgumentException("Unknown sink: $sinkId") }
    }

    private fun createTransform(spec: Specification): Transform {
        return Transform to {
            spec
        } with {
            for (cf in customFunctions) {
                function(cf.id, cf::implementation)
            }
        }
    }

    fun start(sourceId: String, sinkId: String, spec: Specification) {
        val jobId = "$sourceId-$sinkId"

        // TODO: Cancel and replace?
        require(!mappingJobs.containsKey(jobId)) {"Job for mapping $jobId is already running"}

        val source = createSource(sourceId)
        val sink = createSink(sinkId)
        val transform = createTransform(spec)

        val job = coroutineScope.launch {
            while (isActive) {
                if (source.hasData()) {
                    val data = source.fetch()

                    val transformed = transform.apply(data) as Map<String, String>

                    sink.put(transformed)
                } else {
                    delay(100)
                }
            }
        }

        mappingJobs[jobId] = job
    }

    fun cancel(sourceId: String, sinkId: String) {
        val jobId = "$sourceId-$sinkId"
        val mappingJob = mappingJobs[jobId] ?: throw IllegalArgumentException("No job found for mapping: $jobId")

        mappingJob.cancel()
        mappingJobs.remove(jobId)
    }
}