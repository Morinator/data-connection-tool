package com.digitalfrontiers.persistence

import com.digitalfrontiers.transform.Specification
import java.time.LocalDateTime

data class NestedData(
    val id: Long? = null,
    val name: String,
    val data: Specification,
    val createdAt: LocalDateTime = LocalDateTime.now()
)