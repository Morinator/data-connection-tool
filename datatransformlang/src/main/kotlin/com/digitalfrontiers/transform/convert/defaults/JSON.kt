package com.digitalfrontiers.transform.convert.defaults

import com.digitalfrontiers.transform.convert.IParser
import com.digitalfrontiers.transform.convert.ISerializer
import com.digitalfrontiers.util.mapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option

class JSONParser: IParser<Any> {
    override fun parse(string: String): Any? {
        return Configuration
            .defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .addOptions(Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider()
            .parse(string)
    }
}

class JSONSerializer: ISerializer<Any> {
    override fun serialize(data: Any?): String {
        return mapper.writeValueAsString(data)
    }
}