package com.example.presentation.customer_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Customer
import com.example.domain.model.MessageHistory
import com.example.domain.repository.CustomerRepository
import com.example.domain.repository.MessageHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerDetailViewModel(
    private val customerRepository: CustomerRepository,
    private val messageHistoryRepository: MessageHistoryRepository
) : ViewModel() {

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    private val _history = MutableStateFlow<List<MessageHistory>>(emptyList())
    val history: StateFlow<List<MessageHistory>> = _history.asStateFlow()

    fun loadCustomerData(customerId: Long) {
        viewModelScope.launch {
            customerRepository.getCustomerById(customerId).collect {
                _customer.value = it
            }
        }
        viewModelScope.launch {
            messageHistoryRepository.getHistoryForCustomer(customerId).collect {
                _history.value = it
            }
        }
    }

    fun markOilChanged(id: Long, newReminderDate: Long) {
        viewModelScope.launch {
            val c = customerRepository.getCustomerByIdSync(id)
            if (c != null) {
                val updatedCustomer = c.copy(
                    nextReminderDate = newReminderDate,
                    status = com.example.domain.model.CustomerStatus.WAITING
                )
                customerRepository.updateCustomer(updatedCustomer)
            }
        }
    }
}

class CustomerDetailViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val messageHistoryRepository: MessageHistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerDetailViewModel(customerRepository, messageHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
