package com.example.foodflow.data.model

data class MenuItem(
    val id: String = "",
    val restaurantId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
