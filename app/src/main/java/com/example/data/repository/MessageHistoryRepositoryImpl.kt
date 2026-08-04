package com.example.data.repository

import com.example.data.local.dao.MessageHistoryDao
import com.example.data.local.entity.toDomain
import com.example.data.local.entity.toEntity
import com.example.domain.model.MessageHistory
import com.example.domain.repository.MessageHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageHistoryRepositoryImpl(
    private val dao: MessageHistoryDao
) : MessageHistoryRepository {

    override fun getHistoryForCustomer(customerId: Long): Flow<List<MessageHistory>> {
        return dao.getHistoryForCustomer(customerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMessageHistory(history: MessageHistory) {
        dao.insertMessageHistory(history.toEntity())
    }
}
