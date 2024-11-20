package com.digitalfrontiers.dataconnectiontool.components

import com.digitalfrontiers.dataconnectiontool.Format
import org.springframework.stereotype.Component

// ============================

@Component
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