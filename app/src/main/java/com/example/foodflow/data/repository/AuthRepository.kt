package com.example.foodflow.data.repository

import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(email: String, password: String, role: UserRole): Result<AppUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult?.user?.uid ?: throw Exception("User creation failed!")

            val newUser = AppUser(uid, email, role)

            firestore.collection("users").document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}