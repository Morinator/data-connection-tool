package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.dataconnectiontool.extension.Formats
import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVParser
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefaultTransformationService(
    @Autowired private val parsers: Map<String, IParser<Any>>, // Injection der Parser-Map
    @Autowired private val serializers: Map<String, ISerializer<Any>> // Analog für Serializer
): ITransformationService<String, String> {

    override fun transform(data: String, spec: Specification, inputFormat: String?, outputFormat: String?): String {

        val transform = Transform().withSpecification(spec)

        if (inputFormat != null) {

            val parser = parsers[inputFormat + "Parser"]

            require(parser != null) {"No parser set for Format: $inputFormat"}

            transform.withParser(parser)
        }

        if (outputFormat != null) {
            val serializer = serializers[outputFormat + "Serializer"]

            require(serializer != null) {"No serializer set for Format: $outputFormat"}

            transform.withSerializer(serializer)
        }

        return transform.apply(data)
    }
}