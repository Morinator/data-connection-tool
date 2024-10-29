package com.digitalfrontiers.datatransformlang.util.convert

interface Serializer<T> {
    fun serialize(data: T?): String
}