package com.digitalfrontiers.components

import com.digitalfrontiers.Format
import com.digitalfrontiers.JSONFlattener
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8


@Component
class DummySource : ISource {

    override val id = "Dummy"

    override val format =
        Format(
            listOf("a", "b"),
            listOf("c")
        )

    override fun hasData(): Boolean = true

    override fun fetch(): Map<String, String> {
        return mapOf(
            "a" to "A",
            "b" to "B",
            "c" to "C"
        )
    }
}

@Component
class JSONSource : ISource {
    override val id = "JSONSource"

    override val format = Format(listOf(), listOf())

    override fun hasData(): Boolean = true

    override fun fetch(): Map<String, String> {
        val filePath = "dummy_data/json/bla.json"
        return JSONFlattener().flattenJsonFromFile(filePath) as Map<String, String>
    }

}

@Endpoint
class LocalStackS3Source : ISource {

    override val id = "localstackS3"

    override val format = Format(
        listOf("value"),
        emptyList()
    )

    private val mapper = ObjectMapper()

    private val dataList = parseStringToMaps(readStringFromS3("my-bucket", "data.json"))


    private final fun readStringFromS3(bucketName: String, key: String): String {
        val localstackClient = S3Client.builder()
            .region(Region.EU_CENTRAL_1)
            .endpointOverride(URI("http://localhost:4566")) // default of LocalStack
            .forcePathStyle(true)  // use path-style addressing
            .build()

        val request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        return localstackClient.getObject(request).readBytes().toString(UTF_8)
    }

    private fun parseStringToMaps(data: String): MutableList<Map<String, String>> {
        val originalData: Map<*, *>? = mapper.readValue(data, Map::class.java)
        val porsche: Map<String, *> = originalData?.get("porsche") as Map<String, *>
        val models: Map<String, *> = porsche["models"] as Map<String, *>
        val v1: List<Map<String, *>> = models["v1"] as List<Map<String, *>>

        val v1WithStringValues: List<Map<String, String>> =
            v1.map {
                it.mapValues { (_, value) ->
                    value.toString()
                }
            }

        return mutableListOf(*v1WithStringValues.toTypedArray())
    }

    override fun hasData(): Boolean = dataList.isNotEmpty()

    override fun fetch(): Map<String, String> {
        return dataList.removeFirst()
    }
}

fun main() {
//    val content = LocalStackS3Source().fetch()
//    println("Content from S3: $content")

    val jsonSource = JSONSource()
    val stringMap = jsonSource.fetch()
    for (key in stringMap.keys) {
        println("$key -> ${stringMap[key]}")
    }
}