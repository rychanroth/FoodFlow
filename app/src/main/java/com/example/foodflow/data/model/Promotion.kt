package com.example.foodflow.data.model

data class Promotion(
    val id: String = "",
    val restaurantId: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)