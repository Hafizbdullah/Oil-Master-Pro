package com.example.domain.model

data class MessageHistory(
    val id: Long = 0,
    val customerId: Long,
    val sendTime: Long,
    val status: String, // "نجاح" or "فشل"
    val failureReason: String? = null
)
