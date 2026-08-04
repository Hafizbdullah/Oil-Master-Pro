package com.example.data.repository

import com.example.data.local.dao.CustomerDao
import com.example.data.local.entity.toDomain
import com.example.data.local.entity.toEntity
import com.example.domain.model.Customer
import com.example.domain.model.CustomerStatus
import com.example.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomerRepositoryImpl(
    private val dao: CustomerDao
) : CustomerRepository {

    override fun getAllCustomers(): Flow<List<Customer>> {
        return dao.getAllCustomers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCustomerById(id: Long): Flow<Customer?> {
        return dao.getCustomerById(id).map { it?.toDomain() }
    }

    override suspend fun getCustomerByIdSync(id: Long): Customer? {
        return dao.getCustomerByIdSync(id)?.toDomain()
    }

    override suspend fun insertCustomer(customer: Customer): Long {
        return dao.insertCustomer(customer.toEntity())
    }

    override suspend fun updateCustomer(customer: Customer) {
        dao.updateCustomer(customer.toEntity())
    }

    override suspend fun deleteCustomerById(id: Long) {
        dao.deleteCustomerById(id)
    }

    override suspend fun updateCustomerStatus(id: Long, status: CustomerStatus) {
        dao.updateCustomerStatus(id, status.name)
    }
}
