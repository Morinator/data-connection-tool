package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer
import com.digitalfrontiers.datatransformlang.with
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefaultTransformationService(
    @Autowired private val parsers: Map<String, IParser<Any>>, // Injection der Parser-Map
    @Autowired private val serializers: Map<String, ISerializer<Any>> // Analog für Serializer
): ITransformationService<String, String> {

    override fun transform(data: String, spec: Specification, inputFormat: String?, outputFormat: String?): String {

        val transform = Transform to {spec}

        val inputFormatFinal = inputFormat ?: "JSON"
        val parser = parsers[inputFormatFinal + "Parser"]

        require(parser != null) {"No parser set for Format: $inputFormatFinal"}

        transform with {
            parserFor(inputFormatFinal) {parser}
        }

        val outputFormatFinal = outputFormat ?: "JSON"
        val serializer = serializers[outputFormatFinal + "Serializer"]

        require(serializer != null) {"No serializer set for Format: $outputFormatFinal"}

        transform with {
            serializerFor(outputFormatFinal) {serializer}
        }

        return transform.apply(data, inputFormatFinal, outputFormatFinal)
    }
}