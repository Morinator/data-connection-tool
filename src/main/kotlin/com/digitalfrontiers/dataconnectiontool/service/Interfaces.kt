package com.digitalfrontiers.dataconnectiontool.service

import com.digitalfrontiers.datatransformlang.transform.Specification

interface IStorageService<I> {
    fun store(key: String, data: I)

    fun load(key: String): I?
}

interface ITransformationService<I, O> {
    fun transform(data: I, spec: Specification): O
}