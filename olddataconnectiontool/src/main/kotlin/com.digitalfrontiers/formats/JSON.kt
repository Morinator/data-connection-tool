package com.digitalfrontiers.formats


import com.digitalfrontiers.transform.convert.IParser
import com.digitalfrontiers.transform.convert.ISerializer
import com.digitalfrontiers.transform.convert.defaults.JSONParser
import com.digitalfrontiers.transform.convert.defaults.JSONSerializer
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