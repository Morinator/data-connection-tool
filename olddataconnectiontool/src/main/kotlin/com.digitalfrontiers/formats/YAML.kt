package com.digitalfrontiers.formats

import com.digitalfrontiers.transform.convert.IParser
import com.digitalfrontiers.transform.convert.ISerializer
import com.digitalfrontiers.transform.convert.defaults.YAMLParser
import com.digitalfrontiers.transform.convert.defaults.YAMLSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class YAMLConfiguration {

    @Bean("YAMLParser")
    fun yamlParser(): IParser<Any> {
        return YAMLParser()
    }

    @Bean("YAMLSerializer")
    fun yamlSerializer(): ISerializer<Any> {
        return YAMLSerializer()
    }
}