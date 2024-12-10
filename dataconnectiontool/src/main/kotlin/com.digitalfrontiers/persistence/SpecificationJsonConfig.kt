package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Mixin abstract class that defines the JSON type information.
 * These annotations will be applied to the Specification class at runtime.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Specification.Self::class, name = "Self"),
    JsonSubTypes.Type(value = Specification.Const::class, name = "Const"),
    JsonSubTypes.Type(value = Specification.Input::class, name = "Input"),
    JsonSubTypes.Type(value = Specification.Tuple::class, name = "Tuple"),
    JsonSubTypes.Type(value = Specification.Record::class, name = "Record"),
    JsonSubTypes.Type(value = Specification.ListOf::class, name = "ListOf"),
    JsonSubTypes.Type(value = Specification.Extension::class, name = "Extension"),
    JsonSubTypes.Type(value = Specification.Rename::class, name = "Rename"),
    JsonSubTypes.Type(value = Specification.ResultOf::class, name = "ResultOf"),
    JsonSubTypes.Type(value = Specification.Compose::class, name = "Compose")
)
abstract class SpecificationMixin

class SpecificationJsonConfig {
    companion object {
        private fun configureMapper(mapper: ObjectMapper = ObjectMapper()): ObjectMapper {

            // Register the mixin!
            // It tells Jackson to apply the annotations from SpecificationMixin to the Specification class
            mapper.addMixIn(Specification::class.java, SpecificationMixin::class.java)

            return mapper
        }

        fun createMapper(): ObjectMapper = configureMapper(ObjectMapper())
   }
}