package com.digitalfrontiers.dataconnectiontool.components

import com.digitalfrontiers.dataconnectiontool.Format
import org.springframework.stereotype.Component

interface ISink {
    val id: String

    val format: Format

    fun process(data: Map<String, String>)
}

// ============================

@Component
class DummySink: ISink {
    override val id = "Dummy"

    override val format =
        Format(
            listOf("x"),
            listOf("y", "z")
        )

    override fun process(data: Map<String, String>) {
        return
    }
}