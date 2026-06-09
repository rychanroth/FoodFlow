package com.example.foodflow.data.model

data class MenuItemCategory(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)