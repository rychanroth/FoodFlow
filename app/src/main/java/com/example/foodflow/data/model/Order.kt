package com.example.foodflow.data.model

data class Order(
    val id: String = "",
    val customerId: String = "",
    val restaurantId: String = "",
    val items: List<String> = emptyList(), // Simplified for Firestore compatibility
    val totalAmount: Double = 0.0,
    val status: String = "PLACED",
    val createdAt: Long = System.currentTimeMillis()
)
