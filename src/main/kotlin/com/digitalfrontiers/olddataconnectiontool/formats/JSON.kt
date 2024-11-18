package com.digitalfrontiers.olddataconnectiontool.formats

import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.JSONParser
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.JSONSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JSONConfiguration {
    @Bean("JSONParser")
    fun jsonParser(): IParser<Any> {
        return JSONParser()
    }

    @Bean("JSONSerializer")
    fun jsonSerializer(): ISerializer<Any> {
        return JSONSerializer()
    }
}