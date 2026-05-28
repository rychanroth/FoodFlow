package com.example.foodflow.data.model

enum class ApplicationStatus { PENDING, APPROVED, REJECTED }

data class Application(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val requestedRole: UserRole = UserRole.DRIVER, // Can only request DRIVER or RESTAURANT
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val businessDetails: String = "", // E.g., Restaurant name, Driver license ID
    val createdAt: Long = System.currentTimeMillis()
)