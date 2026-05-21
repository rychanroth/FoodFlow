package com.example.foodflow.data.repository

import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    suspend fun registerUser(email: String, password: String, role: UserRole): Result<AppUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")
            val newUser = AppUser(uid = uid, email = email, role = role)
            firestore.collection("users").document(uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UserRole> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Login failed")

            // Fetch the user's role from Firestore to route them correctly
            val document = firestore.collection("users").document(uid).get().await()
            val appUser = document.toObject(AppUser::class.java)

            if (appUser != null) {
                Result.success(appUser.role)
            } else {
                Result.failure(Exception("User data not found in database"))
            }
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getAuthErrorMessage(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address format."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_WRONG_PASSWORD" -> "Incorrect Password."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email."
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again later."
            else -> e.message ?: "Authentication failed."
        }
    }
}