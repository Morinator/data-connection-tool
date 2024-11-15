package com.digitalfrontiers.dataconnectiontool.extension

import com.digitalfrontiers.datatransformlang.transform.convert.IParser
import com.digitalfrontiers.datatransformlang.transform.convert.ISerializer
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVParser
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CSVConfiguration {

    @Bean("CSVParser")
    fun csvParser(): IParser<Any> {
        return CSVParser()
    }

    @Bean("CSVSerializer")
    fun csvSerializer(): ISerializer<Any> {
        return CSVSerializer()
    }
}
