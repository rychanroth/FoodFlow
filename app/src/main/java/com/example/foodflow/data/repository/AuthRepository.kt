package com.example.foodflow.data.repository

import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    suspend fun registerUser(email: String, password: String): Result<AppUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")
            val newUser = AppUser(uid = uid, email = email, role = UserRole.CUSTOMER)
            firestore.collection("users").document(uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NEW: Google Sign-In function
    suspend fun firebaseAuthWithGoogle(idToken: String): Result<AppUser> {
        return try {
            // 1. Exchange the Google token for a Firebase credential
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)

            // 2. Sign in to Firebase with the credential
            val authResult = auth.signInWithCredential(credential).await()
            val uid = authResult.user?.uid ?: throw Exception("Google sign-in failed")

            // 3. Check if this is a new user or existing user
            val userDoc = firestore.collection("users").document(uid).get().await()

            if (userDoc.exists()) {
                // Existing User: Just log them in
                val appUser = userDoc.toObject(AppUser::class.java) ?: throw Exception("Data parse error")
                Result.success(appUser)
            } else {
                // New User: Create their profile in Firestore as CUSTOMER
                val email = authResult.user?.email ?: ""
                val newUser = AppUser(uid = uid, email = email, role = UserRole.CUSTOMER)
                firestore.collection("users").document(uid).set(newUser).await()
                Result.success(newUser)
            }
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