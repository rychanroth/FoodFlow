package com.example.foodflow.data.model

data class MenuItem(
    val id: String = "",
    val restaurantId: String = "",
    val categoryId: String = "", // NEW V3
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val estimatedPrepTime: Int = 0, // NEW V3 (in minutes)
    val isActive: Boolean = true,   // NEW V3: Availability toggle
    val createdAt: Long = System.currentTimeMillis()
)
