package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Customer
import com.example.domain.model.CustomerStatus

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val oilImageUri: String?,
    val nextReminderDate: Long,
    val status: String,
    val notes: String
)

fun CustomerEntity.toDomain() = Customer(
    id = id,
    name = name,
    phone = phone,
    oilImageUri = oilImageUri,
    nextReminderDate = nextReminderDate,
    status = CustomerStatus.valueOf(status),
    notes = notes
)

fun Customer.toEntity() = CustomerEntity(
    id = id,
    name = name,
    phone = phone,
    oilImageUri = oilImageUri,
    nextReminderDate = nextReminderDate,
    status = status.name,
    notes = notes
)
