package com.digitalfrontiers.config


import com.digitalfrontiers.transform.Transformation
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig : Jackson2ObjectMapperBuilderCustomizer {
    override fun customize(builder: org.springframework.http.converter.json.Jackson2ObjectMapperBuilder) {
        builder.mixIn(Transformation::class.java, TransformationMixin::class.java)
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Transformation.Self::class, name = "self"),
    JsonSubTypes.Type(value = Transformation.Const::class, name = "const"),
    JsonSubTypes.Type(value = Transformation.Input::class, name = "input"),
    JsonSubTypes.Type(value = Transformation.Tuple::class, name = "tuple"),
    JsonSubTypes.Type(value = Transformation.Record::class, name = "record"),
    JsonSubTypes.Type(value = Transformation.ListOf::class, name = "listOf"),
    JsonSubTypes.Type(value = Transformation.Extension::class, name = "extension"),
    JsonSubTypes.Type(value = Transformation.Rename::class, name = "rename"),
    JsonSubTypes.Type(value = Transformation.ResultOf::class, name = "resultOf"),
    JsonSubTypes.Type(value = Transformation.Compose::class, name = "compose")
)
abstract class TransformationMixin
