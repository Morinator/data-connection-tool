package com.digitalfrontiers.datatransformlang

import com.digitalfrontiers.datatransformlang.transform.*

fun main() {
    val spec = ToObject {
        "a" to 3
        "b" from "$.y"
        "c" from "$[*].y"
        "d" (
            1,
            ToObject {
                "p" from "$.q"
            }
        )
        "e" {
            "f" to 5
            "g" (2, 3)
        }
    }

    println("Done!")
}