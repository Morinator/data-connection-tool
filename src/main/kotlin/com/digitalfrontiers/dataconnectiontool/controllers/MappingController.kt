package com.digitalfrontiers.dataconnectiontool.controllers

import com.digitalfrontiers.dataconnectiontool.services.MappingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("mappings")
class MappingController(
    @Autowired private val mappingService: MappingService
) {

    @PostMapping("/transfer")
    fun callMapping() {
        mappingService.transfer("Dummy", "Dummy")
    }
}