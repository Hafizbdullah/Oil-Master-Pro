package com.example.domain.repository

import com.example.domain.model.MessageHistory
import kotlinx.coroutines.flow.Flow

interface MessageHistoryRepository {
    fun getHistoryForCustomer(customerId: Long): Flow<List<MessageHistory>>
    suspend fun insertMessageHistory(history: MessageHistory)
}
