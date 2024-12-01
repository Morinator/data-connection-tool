package com.digitalfrontiers.components

import com.digitalfrontiers.transform.convert.defaults.JSONSerializer
import org.springframework.stereotype.Component
import java.io.File


interface Sink {
    val id: String

    val format: Format

    fun put(data: List<Map<String, String>>)
}

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