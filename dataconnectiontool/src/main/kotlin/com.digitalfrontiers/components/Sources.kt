package com.digitalfrontiers.components

import com.digitalfrontiers.Format
import com.digitalfrontiers.JSONFlattener
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8


@Component
class DummySource: ISource {

    override val id = "Dummy"

    override val format =
        Format(
            listOf("a", "b"),
            listOf("c")
        )

    override fun fetch(): Map<String, String> {
        return mapOf(
            "a" to "A",
            "b" to "B",
            "c" to "C"
        )
    }
}

@Component
class JSONSource: ISource {
    override val id = "JSONSource"

    override val format = Format(listOf(), listOf())

    override fun fetch(): Map<String, String> {
        val filePath = "dummy_data/json/bla.json"
        return JSONFlattener().flattenJsonFromFile(filePath) as Map<String, String>
    }

}


class LocalStackS3Source : ISource {

    override val id = "localstackS3"

    override val format = Format(
            listOf("value"),
            emptyList()
        )

    private val localstackClient: S3Client = S3Client.builder()
        .endpointOverride(URI("http://localhost:4566")) // default of LocalStack
        .forcePathStyle(true)  // use path-style addressing
        .build()

    fun readStringFromS3(bucketName: String, key: String): String {
        val request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        return localstackClient.getObject(request).readBytes().toString(UTF_8)
    }

    override fun fetch(): Map<String, String> {
        return mapOf("value" to readStringFromS3("my-bucket", "my-file.txt"))
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