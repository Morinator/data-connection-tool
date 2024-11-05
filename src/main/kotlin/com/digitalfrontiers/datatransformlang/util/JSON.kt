package com.digitalfrontiers.datatransformlang.util

import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option

internal val mapper = jacksonObjectMapper()

object JSON : IParser<Any>, ISerializer<Any> {

    override fun parse(string: String): Any? {
        return Configuration
            .defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .addOptions(Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider()
            .parse(string)
    }

    override fun serialize(data: Any?): String {
        return mapper.writeValueAsString(data)
    }

    fun toJSONLike(data: Any?): Any? {
        return if (data is List<*>)
            mapper.convertValue(data, List::class.java)
        else
            mapper.convertValue(data, Map::class.java)
    }

}