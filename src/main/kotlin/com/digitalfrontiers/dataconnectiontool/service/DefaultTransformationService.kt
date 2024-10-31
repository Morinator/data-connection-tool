package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.dataconnectiontool.extension.Formats
import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVParser
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVSerializer
import org.springframework.stereotype.Service

@Service
class DefaultTransformationService: ITransformationService<String, String> {

    init {
        Formats.setParserFor("CSV", CSVParser())
        Formats.setSerializerFor("CSV", CSVSerializer())
    }

    override fun transform(data: String, spec: Specification, inputFormat: String?, outputFormat: String?): String {

        val transform = Transform().withSpecification(spec)

        if (inputFormat != null) {
            val parser = Formats.getParserFor<Any>(inputFormat)

            require(parser != null) {"No parser set for Format: $inputFormat"}

            transform.withParser(parser)
        }

        if (outputFormat != null) {
            val serializer = Formats.getSerializerFor<Any>(outputFormat)

            require(serializer != null) {"No serializer set for Format: $outputFormat"}

            transform.withSerializer(serializer)
        }

        return transform.apply(data)
    }
}