package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.MessageHistory

@Entity(tableName = "message_history")
data class MessageHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val sendTime: Long,
    val status: String,
    val failureReason: String? = null
)

fun MessageHistoryEntity.toDomain() = MessageHistory(
    id = id,
    customerId = customerId,
    sendTime = sendTime,
    status = status,
    failureReason = failureReason
)

fun MessageHistory.toEntity() = MessageHistoryEntity(
    id = id,
    customerId = customerId,
    sendTime = sendTime,
    status = status,
    failureReason = failureReason
)
