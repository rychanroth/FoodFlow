package com.example.foodflow.data.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String = "",
    val addresses: List<Address> = emptyList()
)

// Placeholder for addresses
data class Address(
    val id: String = "",
    val street: String = "",
    val city: String = "",
    val isDefault: Boolean = false
)