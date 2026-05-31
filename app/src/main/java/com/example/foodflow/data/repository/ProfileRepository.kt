package com.example.foodflow.data.repository

import com.example.foodflow.data.model.AppUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(userId: String): Result<AppUser> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            val user = document.toObject(AppUser::class.java) ?: AppUser()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}