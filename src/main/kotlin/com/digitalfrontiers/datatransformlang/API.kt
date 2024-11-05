package com.digitalfrontiers.datatransformlang

import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.applyTransform
import com.digitalfrontiers.datatransformlang.util.JSON
import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer
import java.io.File

class Transform {
    private var spec: Specification? = null
    private var parser: IParser<*> = JSON
    private var serializer: ISerializer<*> = JSON

    fun withSpecification(spec: Specification): Transform {
        this.spec = spec

        return this
    }

    fun withParser(parser: IParser<Any>): Transform {
        this.parser = parser

        return this
    }

    fun withSerializer(serializer: ISerializer<Any>): Transform {
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
        return (this.serializer as ISerializer<Any>).serialize(result)
    }

    fun apply(file: File): String {
        val contents = file.inputStream().readBytes().toString(Charsets.UTF_8)

        return this.apply(contents)
    }
}