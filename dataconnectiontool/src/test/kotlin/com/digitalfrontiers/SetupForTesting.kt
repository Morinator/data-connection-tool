package com.digitalfrontiers

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.components.Sink
import com.digitalfrontiers.components.Source
import com.digitalfrontiers.transform.convert.defaults.JSONSerializer
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Only appends the data in the list [storage] field and does not offer actual persistence.
 */
@Component
class DummySink : Sink {

    val storage: MutableList<List<Map<String, String>>> = ArrayList()

    override val id = "Dummy"

    override val format =
        Format(
            listOf("x"),
            listOf("y", "z")
        )

    override fun put(data: List<Map<String, String>>) {
        storage.add(data)
    }
}

@Component
class JSONSink : Sink {

    private val js = JSONSerializer()
    private val filePath = "dummy_data/json/john_doe_transformed.json"


    override val id = "JSONSink"

    override val format =
        Format(
            listOf(),
            listOf()
        )

    override fun put(data: List<Map<String, String>>) {
        val str = js.serialize(data)

        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeText(str)
    }
}


@Component
class DummySource : Source {

    override val id = "Dummy"

    override val format =
        Format(
            listOf("a", "b"),
            listOf("c")
        )

    override fun fetch(): List<Map<String, String>> {
        return listOf(
            mapOf(
                "a" to "A_value",
                "b" to "B_value",
                "c" to "C_value"
            )
        )
    }
}


class LocalStackS3Source : Source {

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

    override fun fetch(): List<Map<String, String>> {
        return listOf(
            mapOf("value" to readStringFromS3("my-bucket", "my-file.txt"))
        )
    }
}
