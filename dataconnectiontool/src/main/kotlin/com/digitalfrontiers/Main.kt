package com.digitalfrontiers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.digitalfrontiers.persistence"])
@EntityScan(basePackages = ["com.digitalfrontiers.persistence"])
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}