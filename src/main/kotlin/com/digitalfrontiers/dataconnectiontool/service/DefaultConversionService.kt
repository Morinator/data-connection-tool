package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.dataconnectiontool.extension.Formats
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVParser
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVSerializer
import org.springframework.stereotype.Service

@Service
class DefaultConversionService : IConversionService {

    init {
        Formats.setParserFor("CSV", CSVParser())
        Formats.setSerializerFor("CSV", CSVSerializer())
    }

    override fun <T : Any> parse(format: String, data: String): T? {
        val parser = Formats.getParserFor<T>(format)

        return parser?.parse(data)
    }

    override fun <T : Any> serialize(format: String, data: T?): String? {
        val serializer = Formats.getSerializerFor<T>(format)

        return serializer?.serialize(data)
    }
}