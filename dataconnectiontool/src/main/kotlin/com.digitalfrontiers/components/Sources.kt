package com.digitalfrontiers.components

import com.digitalfrontiers.Format
import org.springframework.stereotype.Component

// ============================

@Component
class DummySource: ISource {

    override val id = "Dummy"

    override val format =
        Format(
            listOf("a", "b"),
            listOf("c")
        )

    override fun fetch(): Map<String, String> {
        return mapOf(
            "a" to "A",
            "b" to "B",
            "c" to "C"
        )
    }
}