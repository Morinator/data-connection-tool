package com.digitalfrontiers.components

import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8

interface Source {
    val id: String

    val format: Format

    fun fetch(): Map<String, String>
}

@Component
class DummySource: Source {

    override val id = "Dummy"

    override val format =
        Format(
            listOf("a", "b"),
            listOf("c")
        )

    override fun fetch(): Map<String, String> {
        return mapOf(
            "a" to "A_value",
            "b" to "B_value",
            "c" to "C_value"
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

    override fun fetch(): Map<String, String> {
        return mapOf("value" to readStringFromS3("my-bucket", "my-file.txt"))
    }
}
