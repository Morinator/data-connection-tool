package com.digitalfrontiers.service

import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException

@Service
class FileStorageService: IStorageService<String> {
    override fun store(key: String, data: String) {
        val file = File("tmp/$key.json")
        file.parentFile.mkdirs()
        file.writeText(data)
    }

    /**
     * @throws FileNotFoundException if no value is present for given [key].
     */
    override fun load(key: String): String? {
        return File("tmp/$key.json").inputStream().readBytes().toString(Charsets.UTF_8)
    }

}