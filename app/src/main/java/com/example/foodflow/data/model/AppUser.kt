package com.example.foodflow.data.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String = "",
    val addresses: List<Address> = emptyList(),
    val isProfileComplete: Boolean = false, // V3: Onboarding gate
    val fcmToken: String = ""               // V3: Push notifications
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