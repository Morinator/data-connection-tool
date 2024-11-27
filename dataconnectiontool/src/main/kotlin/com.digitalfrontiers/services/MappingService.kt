package com.digitalfrontiers.services

import com.digitalfrontiers.Transform
import com.digitalfrontiers.components.ICustomFunction
import com.digitalfrontiers.components.ISink
import com.digitalfrontiers.components.ISource
import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.with
import kotlinx.coroutines.*
import org.springframework.stereotype.Service

@Service
class MappingService(
    private val sources: List<ISource>,
    private val customFunctions: List<ICustomFunction> = emptyList(),
    private val sinks: List<ISink>
) {

    private val mappingJobs: MutableMap<String, Job> = mutableMapOf()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private fun getSource(sourceId: String) =
        sources.firstOrNull {it.id == sourceId} ?: throw IllegalArgumentException("Unknown source: $sourceId")

    private fun getSink(sinkId: String) =
        sinks.firstOrNull {it.id == sinkId} ?: throw IllegalArgumentException("Unknown source: $sinkId")

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

        val source = getSource(sourceId)
        val sink = getSink(sinkId)
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