package com.digitalfrontiers.components

import com.digitalfrontiers.Format
import org.springframework.stereotype.Component

// ============================

@Endpoint
class DummySink: ISink {
    override val id = "Dummy"

    override val format =
        Format(
            listOf("x"),
            listOf("y", "z")
        )

    override fun put(data: Map<String, String>) {
        return
    }
}

@Endpoint
class Dummy2Sink: ISink {
    override val id = "Dummy2"

    override val format =
        Format(
            listOf("x"),
            listOf("y", "z")
        )

    override fun put(data: Map<String, String>) {
        return
    }
}