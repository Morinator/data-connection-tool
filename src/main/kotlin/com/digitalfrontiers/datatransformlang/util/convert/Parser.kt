package com.digitalfrontiers.datatransformlang.util.convert

interface Parser<T> {
    fun parse(string: String): T?
}