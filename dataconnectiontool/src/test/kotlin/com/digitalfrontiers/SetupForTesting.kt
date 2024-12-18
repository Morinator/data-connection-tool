package com.digitalfrontiers

import com.digitalfrontiers.components.Format
import com.digitalfrontiers.components.Sink
import com.digitalfrontiers.components.Source
import com.digitalfrontiers.transform.convert.defaults.JSONSerializer
import org.springframework.stereotype.Component
import java.io.File

/**
 * Only appends the data in the list [storage] field and does not offer actual persistence.
 */
@Component
class DummySink : Sink {

    val storage: MutableList<List<Map<String, String>>> = ArrayList()

    override val id = "Dummy"

    override val format =
        Format(
            listOf("x", "y"),
            listOf("z")
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
