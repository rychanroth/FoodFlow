package com.example.foodflow.data.model

enum class OrderStatus {
    PLACED,
    PENDING_PAYMENT_VERIFICATION,
    PREPARING,
    READY,
    ON_THE_WAY,
    DELIVERED,
    REJECTED
}