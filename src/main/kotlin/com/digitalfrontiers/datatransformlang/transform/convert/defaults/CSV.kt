package com.digitalfrontiers.datatransformlang.transform.convert.defaults

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.StringReader

import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer

internal val csvMapper = CsvMapper()

class CSVParser: IParser<Any> {

    override fun parse(string: String): Any? {
        val schema = CsvSchema.emptySchema().withHeader()
        val reader = StringReader(string)

        val lines = csvMapper.readerFor(Map::class.java).with(schema).readValues<Map<String, String>>(reader)

        return lines.readAll()
    }
}

class CSVSerializer: ISerializer<Any> {
    override fun serialize(data: Any?): String {
        require(data is List<*>) {"Only Arrays or Lists can be serialized as CSV"}

        val flattenedData: List<MutableMap<String, Any?>> =
            data.map {
                val flattened: MutableMap<String, Any?> = mutableMapOf();
                flattenData(it, "", flattened)

                flattened
            }

        val firstItem = flattenedData.first()

        val csvSchema =
            firstItem
                .keys
                .fold(CsvSchema.Builder()) { builder, fieldName -> builder.addColumn(fieldName)}
                .build()
                .withHeader()

        return csvMapper.writerFor(List::class.java)
            .with(csvSchema)
            .writeValueAsString(flattenedData)
    }

    private fun flattenData(original: Any?, baseKey: String, flattened: MutableMap<String, Any?>) {

        fun createKey(baseKey: String, childKey: String): String =
            if (baseKey == "")
                childKey
            else
                "$baseKey.$childKey"

        fun addToFlattened(key: String, el: Any?) {
            if (isPrimitive(el)) {
                flattened[key] = el
            } else {
                flattenData(el, key, flattened)
            }
        }

        if (original is List<*>) {
            original.forEachIndexed { idx, el ->
                addToFlattened(createKey(baseKey, "[$idx]"), el)
            }
        } else if (original is Map<*, *>) {
            original.forEach { key, el ->
                addToFlattened(createKey(baseKey, key as String), el)
            }
        } else
            return
    }

    private inline fun isPrimitive(obj: Any?): Boolean = obj == null || obj is Boolean || obj is Number || obj is String
}