package com.example.foodflow.data.model

enum class OrderStatus {
    PENDING_PAYMENT_VERIFICATION,
    PLACED,
    PREPARING,
    READY,
    ON_THE_WAY,
    DELIVERED,
    REJECTED
}