package com.example.domain.model

enum class CustomerStatus(val displayName: String) {
    WAITING("بانتظار الموعد"),
    SENT("تم الإرسال"),
    LATE("متأخر")
}
