package com.digitalfrontiers.transform.convert.defaults

import com.digitalfrontiers.transform.convert.IParser
import com.digitalfrontiers.transform.convert.ISerializer
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

internal val yamlMapper = YAMLMapper()

class YAMLParser: IParser<Any> {
    override fun parse(string: String): Any? {
        return try {
            yamlMapper.readValue(string, Any::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class YAMLSerializer: ISerializer<Any> {
    override fun serialize(data: Any?): String {
        return try {
            yamlMapper.writeValueAsString(data)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}