package com.example.foodflow.data.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val role: String = "CUSTOMER" // Default Role
)