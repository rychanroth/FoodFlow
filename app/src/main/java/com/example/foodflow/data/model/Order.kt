package com.example.foodflow.data.model

data class Order(
    val id: String = "",
    val customerId: String = "",
    val restaurantId: String = "",
    val driverId: String? = null, // NEW: Null means no driver has claimed it yet
    val itemNames: List<String> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PLACED,
    val createdAt: Long = System.currentTimeMillis()
)