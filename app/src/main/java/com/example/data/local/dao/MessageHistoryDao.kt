package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MessageHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageHistoryDao {
    @Query("SELECT * FROM message_history WHERE customerId = :customerId ORDER BY sendTime DESC")
    fun getHistoryForCustomer(customerId: Long): Flow<List<MessageHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageHistory(history: MessageHistoryEntity)
}
