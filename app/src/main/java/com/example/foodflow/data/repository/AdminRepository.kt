package com.example.foodflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodflow.data.model.Application
import com.example.foodflow.data.model.ApplicationStatus
import com.example.foodflow.data.model.PlatformSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // Fetch all PENDING applications in real-time
    fun getPendingApplications(): Flow<List<Application>> = callbackFlow {
        val subscription = firestore.collection("applications")
            .whereEqualTo("status", ApplicationStatus.PENDING.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val apps = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Application::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(apps)
            }
        awaitClose { subscription.remove() }
    }

    // Approve Application: Update App Status + Update User Role
    suspend fun approveApplication(applicationId: String, userId: String, requestedRole: String) {
        val batch = firestore.batch()

        // 1. Update Application Status
        val appRef = firestore.collection("applications").document(applicationId)
        batch.update(appRef, "status", ApplicationStatus.APPROVED.name)

        // 2. Update User's Role in Firestore
        val userRef = firestore.collection("users").document(userId)
        batch.update(userRef, "role", requestedRole)

        // Commit the batch
        batch.commit().await()
    }

    // Reject Application: Only update App Status
    suspend fun rejectApplication(applicationId: String) {
        val appRef = firestore.collection("applications").document(applicationId)
        appRef.update("status", ApplicationStatus.REJECTED.name).await()
    }

    // Fetch the current platform settings
    suspend fun getPlatformSettings(): Result<PlatformSettings> {
        return try {
            val document = firestore.collection("configuration").document("platformSettings").get().await()
            val settings = document.toObject(PlatformSettings::class.java) ?: PlatformSettings()
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update the platform settings
    suspend fun updatePlatformSettings(settings: PlatformSettings): Result<Unit> {
        return try {
            firestore.collection("configuration").document("platformSettings").set(settings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}