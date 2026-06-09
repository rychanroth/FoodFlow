package com.example.foodflow.data.repository

import com.example.foodflow.data.model.RoleApplication
import com.example.foodflow.data.model.RoleApplicationStatus
import com.example.foodflow.data.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoleApplicationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val applicationsCollection = firestore.collection("role_applications")

    suspend fun submitApplication(userId: String, userEmail: String, requestedRole: UserRole, businessDetails: String): Result<Unit> {
        return try {
            // Prevent applying for Customer or Admin roles
            if (requestedRole == UserRole.CUSTOMER || requestedRole == UserRole.ADMIN) {
                throw IllegalArgumentException("Invalid role application")
            }

            // Check if user already has a PENDING application for this role
            val existingApplications = applicationsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("requestedRole", requestedRole)
                .whereEqualTo("status", "PENDING")
                .get().await()

            if (!existingApplications.isEmpty) {
                throw Exception("You already have a pending application for this role.")
            }

            val newRoleApplication = RoleApplication(
                userId = userId,
                userEmail = userEmail,
                requestedRole = requestedRole,
                businessDetails = businessDetails,
                status = RoleApplicationStatus.PENDING
            )

            applicationsCollection.add(newRoleApplication).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}