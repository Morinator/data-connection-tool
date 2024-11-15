package com.digitalfrontiers.dataconnectiontool.extension

import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer

object Formats {

    private val parsers: MutableMap<String, IParser<*>> = mutableMapOf()
    private val serializers: MutableMap<String, ISerializer<*>> = mutableMapOf()

    fun <T> setParserFor(format: String, parser: IParser<T>) {
        parsers[format] = parser
    }

    fun <T> setSerializerFor(format: String, serializer: ISerializer<T>) {
        serializers[format] = serializer
    }

    fun <T> getParserFor(format: String): IParser<T>? =
        if (parsers.containsKey(format)) {
            parsers[format] as IParser<T>
        } else {
            null
        }

    fun <T> getSerializerFor(format: String): ISerializer<T>? =
        if (parsers.containsKey(format)) {
            serializers[format] as ISerializer<T>
        } else {
            null
        }
}
