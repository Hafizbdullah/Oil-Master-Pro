package com.example.presentation.customer_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Customer
import com.example.domain.model.CustomerStatus
import com.example.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomerListViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _statusFilter = MutableStateFlow<CustomerStatus?>(null)
    val statusFilter: StateFlow<CustomerStatus?> = _statusFilter

    val uiState: StateFlow<CustomerListUiState> = combine(
        customerRepository.getAllCustomers(),
        _searchQuery,
        _statusFilter
    ) { customers, query, status ->
        var filtered = customers
        if (query.isNotBlank()) {
            filtered = filtered.filter { 
                it.name.contains(query, ignoreCase = true) || it.phone.contains(query)
            }
        }
        if (status != null) {
            filtered = filtered.filter { it.status == status }
        }
        CustomerListUiState.Success(filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerListUiState.Loading
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(status: CustomerStatus?) {
        _statusFilter.value = status
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            customerRepository.deleteCustomerById(id)
        }
    }

    fun markOilChanged(id: Long, newReminderDate: Long) {
        viewModelScope.launch {
            val customer = customerRepository.getCustomerByIdSync(id)
            if (customer != null) {
                val updatedCustomer = customer.copy(
                    nextReminderDate = newReminderDate,
                    status = CustomerStatus.WAITING
                )
                customerRepository.updateCustomer(updatedCustomer)
            }
        }
    }
}

sealed interface CustomerListUiState {
    object Loading : CustomerListUiState
    data class Success(val customers: List<Customer>) : CustomerListUiState
}

class CustomerListViewModelFactory(
    private val customerRepository: CustomerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerListViewModel(customerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
