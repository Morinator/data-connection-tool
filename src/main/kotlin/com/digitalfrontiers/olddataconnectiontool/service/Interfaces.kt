package com.digitalfrontiers.olddataconnectiontool.service

import com.digitalfrontiers.datatransformlang.transform.Specification

interface IStorageService<I> {
    fun store(key: String, data: I)

    fun load(key: String): I?
}

interface ITransformationService<I, O> {
    fun transform(data: I, spec: Specification, inputFormat: String? = null, outputFormat: String? = null): O
}