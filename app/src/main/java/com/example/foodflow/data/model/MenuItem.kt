package com.example.foodflow.data.model

import com.google.firebase.firestore.PropertyName

data class MenuItem(
    val id: String = "",
    val restaurantId: String = "",
    val categoryId: String = "", // NEW V3
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val estimatedPrepTime: Int = 0, // NEW V3 (in minutes)
    // ✅ Force Firebase to use "isActive" instead of "active"
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
