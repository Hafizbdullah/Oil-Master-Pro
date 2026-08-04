package com.example.domain.repository

import com.example.domain.model.Customer
import com.example.domain.model.CustomerStatus
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getAllCustomers(): Flow<List<Customer>>
    fun getCustomerById(id: Long): Flow<Customer?>
    suspend fun getCustomerByIdSync(id: Long): Customer?
    suspend fun insertCustomer(customer: Customer): Long
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomerById(id: Long)
    suspend fun updateCustomerStatus(id: Long, status: CustomerStatus)
}
