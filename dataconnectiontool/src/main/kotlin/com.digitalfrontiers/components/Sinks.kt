package com.digitalfrontiers.components

import com.digitalfrontiers.Format
import com.digitalfrontiers.transform.convert.defaults.JSONSerializer
import org.springframework.stereotype.Component
import java.io.File

// ============================

@Component
class DummySink: ISink {

    val storage : MutableList<Map<String, String>> = ArrayList()

    override val id = "Dummy"

    override val format =
        Format(
            listOf("x"),
            listOf("y", "z")
        )

    override fun put(data: Map<String, String>) {
        storage.add(data)
    }
}

@Component
class JSONSink: ISink {

    private val js = JSONSerializer()
    private val filePath = "dummy_data/json/john_doe_transformed.json"


    override val id = "JSONSink"

    override val format =
        Format(
            listOf(),
            listOf()
        )

    override fun put(data: Map<String, String>) {
        val str = js.serialize(data)

        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeText(str)
    }
}