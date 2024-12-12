package com.digitalfrontiers.config


import com.digitalfrontiers.transform.Specification
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig : Jackson2ObjectMapperBuilderCustomizer {
    override fun customize(builder: org.springframework.http.converter.json.Jackson2ObjectMapperBuilder) {
        builder.mixIn(Specification::class.java, SpecificationMixin::class.java)
    }
}

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
