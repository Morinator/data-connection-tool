package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.datatransformlang.transform.Specification

interface IConversionService {
    fun <T: Any> parse(format: String, data: String): T?

    fun <T: Any> serialize(format: String, data: T?): String?
}

interface IStorageService<I> {
    fun store(key: String, data: I)

    fun load(key: String): I?
}

interface ITransformationService<I, O> {
    fun transform(data: I, spec: Specification): O
}