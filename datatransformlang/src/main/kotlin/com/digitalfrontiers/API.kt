package com.digitalfrontiers

import com.digitalfrontiers.transform.DSL
import com.digitalfrontiers.transform.Transformation
import com.digitalfrontiers.transform.applyTransform
import com.digitalfrontiers.transform.convert.IParser
import com.digitalfrontiers.transform.convert.ISerializer

typealias CustomFunction = (input: List<Any?>) -> Any?

class Transform(val spec: Transformation) {
    private val parsers: MutableMap<String, IParser<*>> = mutableMapOf()
    private val serializers: MutableMap<String, ISerializer<*>> = mutableMapOf()
    private val functions: MutableMap<String, CustomFunction> = mutableMapOf()

    companion object {
        infix fun to(specProvider: DSL.() -> Transformation): Transform {
            return Transform(DSL().specProvider())
        }
    }

    fun withParserFor(format: String, parser: IParser<Any>): Transform {
        this.parsers[format] = parser

        return this
    }

    fun withSerializerFor(format: String, serializer: ISerializer<Any>): Transform {
        this.serializers[format] = serializer

        return this
    }

    fun withFunction(fid: String, impl: (input: List<Any?>) -> Any?): Transform {
        this.functions[fid] = impl

        return this
    }

    fun apply(data: Any): Any? {
        return applyTransform(data, this.spec, this.functions.toMap())
    }

    fun apply(data: Any, outputFormat: String): String {
        val result = applyTransform(data, this.spec, this.functions.toMap())

        // TODO: Figure out how to avoid cast
        return (this.serializers[outputFormat] as ISerializer<Any>).serialize(result)
    }

    fun apply(data: String, inputFormat: String): Any? {
        val parsed = this.parsers[inputFormat]?.parse(data)

        return applyTransform(parsed, this.spec, this.functions.toMap())
    }

    fun apply(data: String, inputFormat: String, outputFormat: String): String {

        val parsed = this.parsers[inputFormat]?.parse(data)

        val result: Any? = applyTransform(parsed, this.spec, this.functions.toMap())

        // TODO: see above
        return (this.serializers[outputFormat] as ISerializer<Any>).serialize(result)
    }
}

infix fun Transform.with(setup: ConfigDSL.() -> Unit): Transform {
    ConfigDSL(this).setup()

    return this
}

class ConfigDSL(private val transform: Transform) {

    fun parserFor(format: String, provider: () -> IParser<Any>) {
        this.transform.withParserFor(format, provider())
    }

    fun serializerFor(format: String, provider: () -> ISerializer<Any>) {
        this.transform.withSerializerFor(format, provider())
    }

    fun function(fid: String, impl: CustomFunction) {
        this.transform.withFunction(fid, impl)
    }
}