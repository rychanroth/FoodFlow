package com.example.foodflow.data.model

import com.google.firebase.firestore.PropertyName

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String = "",
    val addresses: List<Address> = emptyList(),
    // ✅ Force Firebase to use "isProfileComplete" instead of "profileComplete"
    @get:PropertyName("isProfileComplete")
    @set:PropertyName("isProfileComplete")
    var isProfileComplete: Boolean = false,

    // ✅ Force Firebase to use "fcmToken" instead of "fcmtoken"
    @get:PropertyName("fcmToken")
    @set:PropertyName("fcmToken")
    var fcmToken: String = ""
)

// Placeholder for addresses
data class Address(
    val id: String = "",
    val street: String = "",
    val city: String = "",
    @get:PropertyName("isDefault")
    @set:PropertyName("isDefault")
    var isDefault: Boolean = false
)

enum class UserRole {
    CUSTOMER,
    RESTAURANT,
    DRIVER,
    ADMIN // NEW: Super Admin
}