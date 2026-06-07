package com.example.foodflow.data.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val name: String = "", // NEW
    val phone: String = "", // NEW
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

enum class UserRole {
    CUSTOMER,
    RESTAURANT,
    DRIVER,
    ADMIN // NEW: Super Admin
}