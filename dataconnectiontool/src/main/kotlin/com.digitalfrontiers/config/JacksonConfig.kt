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
    JsonSubTypes.Type(value = Specification.Self::class, name = "self"),
    JsonSubTypes.Type(value = Specification.Const::class, name = "const"),
    JsonSubTypes.Type(value = Specification.Input::class, name = "input"),
    JsonSubTypes.Type(value = Specification.Tuple::class, name = "tuple"),
    JsonSubTypes.Type(value = Specification.Record::class, name = "record"),
    JsonSubTypes.Type(value = Specification.ListOf::class, name = "listOf"),
    JsonSubTypes.Type(value = Specification.Extension::class, name = "extension"),
    JsonSubTypes.Type(value = Specification.Rename::class, name = "rename"),
    JsonSubTypes.Type(value = Specification.ResultOf::class, name = "resultOf"),
    JsonSubTypes.Type(value = Specification.Compose::class, name = "compose")
)
abstract class SpecificationMixin
