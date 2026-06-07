package com.example.foodflow.data.model

data class Promotion(
    val id: String = "",
    val restaurantId: String = "",
    val menuItemId: String = "", // NEW V3: Links banner to a specific menu item
    val imageUrl: String = "",
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)