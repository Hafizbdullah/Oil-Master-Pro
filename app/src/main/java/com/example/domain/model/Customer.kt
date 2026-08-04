package com.example.domain.model

data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val oilImageUri: String?,
    val nextReminderDate: Long,
    val status: CustomerStatus,
    val notes: String
)
