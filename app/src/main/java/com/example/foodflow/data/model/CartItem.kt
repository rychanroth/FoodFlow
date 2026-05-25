package com.example.foodflow.data.model

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int = 1
)