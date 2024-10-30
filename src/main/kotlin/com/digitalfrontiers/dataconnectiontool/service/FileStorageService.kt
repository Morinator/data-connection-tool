package com.digitalfrontiers.dataconnectiontool.service

import org.springframework.stereotype.Service
import java.io.File

@Service
class FileStorageService: IStorageService<String> {
    override fun store(key: String, data: String) {
        val file = File("tmp/$key.json")
        file.parentFile.mkdirs()
        file.writeText(data)
    }

    override fun load(key: String): String? {
        return File("tmp/$key.json").inputStream().readBytes().toString(Charsets.UTF_8)
    }

}