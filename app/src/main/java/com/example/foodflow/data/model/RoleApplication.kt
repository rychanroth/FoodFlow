package com.example.foodflow.data.model

enum class RoleApplicationStatus { PENDING, APPROVED, REJECTED }

data class RoleApplication(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val requestedRole: UserRole = UserRole.DRIVER, // Can only request DRIVER or RESTAURANT
    val status: RoleApplicationStatus = RoleApplicationStatus.PENDING,
    val businessDetails: String = "", // E.g., Restaurant name, Driver license ID
    val createdAt: Long = System.currentTimeMillis()
)