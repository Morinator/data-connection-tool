package com.digitalfrontiers.persistence

import java.time.LocalDateTime

data class NestedData(
    val id: Long? = null,
    val name: String,
    val data: List<List<Map<String, Any>>>,
    val createdAt: LocalDateTime = LocalDateTime.now()
)