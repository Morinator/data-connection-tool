package com.digitalfrontiers.datatransformlang

import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.applyTransform
import com.digitalfrontiers.datatransformlang.util.convert.JSON
import com.digitalfrontiers.datatransformlang.util.convert.Parser
import com.digitalfrontiers.datatransformlang.util.convert.Serializer
import java.io.File

class Transform {
    private var spec: Specification? = null
    private var parser: Parser<*> = JSON
    private var serializer: Serializer<*> = JSON

    fun withSpecification(spec: Specification): Transform {
        this.spec = spec

        return this
    }

    fun <T> withParser(parser: Parser<T>): Transform {
        this.parser = parser

        return this
    }

    fun <T> withSerializer(serializer: Serializer<T>): Transform {
        this.serializer = serializer

        return this
    }

    fun apply(string: String): String {

        require(this.spec != null) {"No transformation spec given"}

        var parsed = this.parser.parse(string)

        if (this.parser != JSON) {
            parsed = JSON.toJSONLike(parsed)
        }

        val result: Any? = applyTransform(parsed, this.spec!!)

        // TODO: Figure out how to avoid cast
        return (this.serializer as Serializer<Any>).serialize(result)
    }

    fun apply(file: File): String {
        val contents = file.inputStream().readBytes().toString(Charsets.UTF_8)

        return this.apply(contents)
    }
}