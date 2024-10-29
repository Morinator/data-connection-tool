package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.Specification
import org.springframework.stereotype.Service

@Service
class JSONTransformationService: TransformationService<String, String> {

    override fun transform(data: String, spec: Specification): String {
        return Transform()
                .withSpecification(spec)
                .apply(data)
    }
}