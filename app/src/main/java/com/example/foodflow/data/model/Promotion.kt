package com.example.foodflow.data.model

import com.google.firebase.firestore.PropertyName

data class Promotion(
    val id: String = "",
    val restaurantId: String = "",
    val menuItemId: String = "", // NEW V3: Links banner to a specific menu item
    val imageUrl: String = "",
    // ✅ Force Firebase to use "isActive" instead of "active"
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = false,
    @get:PropertyName("isRejected")
    @set:PropertyName("isRejected")
    var isRejected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)