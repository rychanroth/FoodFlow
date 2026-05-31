package com.example.foodflow.data.repository

import com.example.foodflow.data.model.PlatformSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ConfigRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getPlatformSettings(): Result<PlatformSettings> {
        return try {
            val document = firestore.collection("configuration").document("platformSettings").get().await()
            val settings = document.toObject(PlatformSettings::class.java) ?: PlatformSettings()
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}