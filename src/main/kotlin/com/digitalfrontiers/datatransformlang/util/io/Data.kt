package com.digitalfrontiers.datatransformlang.util.io

import java.io.File

fun toFile(file: File, contents: String) {
    file.apply {
        parentFile?.mkdirs() // Create parent directories if they don't exist
        writeText(contents) // Write the serialized data
    }
}