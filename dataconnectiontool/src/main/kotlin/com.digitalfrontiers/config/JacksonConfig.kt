package com.digitalfrontiers.config


import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.persistence.SpecificationMixin
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig : Jackson2ObjectMapperBuilderCustomizer {
    override fun customize(builder: org.springframework.http.converter.json.Jackson2ObjectMapperBuilder) {
        builder.mixIn(Specification::class.java, SpecificationMixin::class.java)
    }
}

