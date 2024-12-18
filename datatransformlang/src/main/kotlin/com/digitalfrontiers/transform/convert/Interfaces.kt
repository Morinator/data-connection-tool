package com.digitalfrontiers.transform.convert

interface IParser<T> {
    fun parse(string: String): T?
}

interface ISerializer<T> {
    fun serialize(data: T?): String
}