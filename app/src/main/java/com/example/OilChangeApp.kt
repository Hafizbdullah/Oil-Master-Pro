package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.CustomerRepositoryImpl
import com.example.data.repository.MessageHistoryRepositoryImpl
import com.example.domain.repository.CustomerRepository
import com.example.domain.repository.MessageHistoryRepository

class OilChangeApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var customerRepository: CustomerRepository
        private set

    lateinit var messageHistoryRepository: MessageHistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        customerRepository = CustomerRepositoryImpl(database.customerDao())
        messageHistoryRepository = MessageHistoryRepositoryImpl(database.messageHistoryDao())
    }
}
