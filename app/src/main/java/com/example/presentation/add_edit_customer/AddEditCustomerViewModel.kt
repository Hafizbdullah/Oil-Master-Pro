package com.example.presentation.add_edit_customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Customer
import com.example.domain.model.CustomerStatus
import com.example.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditCustomerViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditCustomerUiState())
    val uiState: StateFlow<AddEditCustomerUiState> = _uiState.asStateFlow()

    fun loadCustomer(id: Long) {
        if (id == -1L) return
        viewModelScope.launch {
            val customer = customerRepository.getCustomerByIdSync(id)
            if (customer != null) {
                _uiState.update {
                    it.copy(
                        id = customer.id,
                        name = customer.name,
                        phone = customer.phone,
                        oilImageUri = customer.oilImageUri,
                        nextReminderDate = customer.nextReminderDate,
                        notes = customer.notes
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditCustomerEvent) {
        when (event) {
            is AddEditCustomerEvent.NameChanged -> _uiState.update { it.copy(name = event.name) }
            is AddEditCustomerEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.phone) }
            is AddEditCustomerEvent.OilImageChanged -> _uiState.update { it.copy(oilImageUri = event.uri) }
            is AddEditCustomerEvent.DateChanged -> _uiState.update { it.copy(nextReminderDate = event.date) }
            is AddEditCustomerEvent.NotesChanged -> _uiState.update { it.copy(notes = event.notes) }
            is AddEditCustomerEvent.Save -> saveCustomer(event.onSuccess)
        }
    }

    private fun saveCustomer(onSuccess: (Long) -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank() || state.phone.isBlank() || state.nextReminderDate == null) {
            return
        }

        val customer = Customer(
            id = state.id ?: 0L,
            name = state.name,
            phone = state.phone,
            oilImageUri = state.oilImageUri,
            nextReminderDate = state.nextReminderDate,
            status = CustomerStatus.WAITING,
            notes = state.notes
        )

        viewModelScope.launch {
            val savedId = customerRepository.insertCustomer(customer)
            onSuccess(savedId)
        }
    }
}

data class AddEditCustomerUiState(
    val id: Long? = null,
    val name: String = "",
    val phone: String = "",
    val oilImageUri: String? = null,
    val nextReminderDate: Long? = null,
    val notes: String = ""
)

sealed interface AddEditCustomerEvent {
    data class NameChanged(val name: String) : AddEditCustomerEvent
    data class PhoneChanged(val phone: String) : AddEditCustomerEvent
    data class OilImageChanged(val uri: String?) : AddEditCustomerEvent
    data class DateChanged(val date: Long) : AddEditCustomerEvent
    data class NotesChanged(val notes: String) : AddEditCustomerEvent
    data class Save(val onSuccess: (Long) -> Unit) : AddEditCustomerEvent
}

class AddEditCustomerViewModelFactory(
    private val customerRepository: CustomerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditCustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditCustomerViewModel(customerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
