package com.digitalfrontiers.util

import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.persistence.SpecificationJsonConfig
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

private val mapper: ObjectMapper = SpecificationJsonConfig.createMapper()

fun parseTransformConfig(specString: String): Specification {
    return mapper.readValue(specString, Specification::class.java)
}

fun parseTransformNode(node: JsonNode): Specification {
    return mapper.treeToValue(node, Specification::class.java)
}
