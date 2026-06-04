package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check if user is already logged in (returning user)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                val result = repository.getUserRole(currentUser.uid)
                if (result.isSuccess) {
                    _authState.value = AuthState.Success(result.getOrNull() ?: UserRole.CUSTOMER)
                } else {
                    _authState.value = AuthState.Idle // Force login if data is corrupt
                }
            }
        }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Fields cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            // 1. Create the user
            val regResult = repository.registerUser(email, password)

            if (regResult.isSuccess) {
                // 2. Send verification email
                val verifyResult = repository.sendEmailVerification()
                if (verifyResult.isSuccess) {
                    // 3. Sign them out so they HAVE to verify before entering
                    repository.logout()
                    _authState.value = AuthState.AwaitingVerification
                } else {
                    _authState.value = AuthState.Error("Registered, but failed to send verification email.")
                }
            } else {
                val errorMsg = regResult.exceptionOrNull()?.message ?: "Unknown error"
                // V2 UX: Check if the error is because the email is already in use
                if (errorMsg.contains("already in use", ignoreCase = true)) {
                    _authState.value = AuthState.Error("This email is already registered. Try logging in or resetting your password.")
                } else {
                    _authState.value = AuthState.Error(errorMsg)
                }
            }
        }
    }

    // NEW: Handle Google Sign-In
    fun googleSignIn(idToken: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.firebaseAuthWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _authState.value = AuthState.Success(user?.role ?: UserRole.CUSTOMER)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Google Sign-In failed")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Fields cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                // V2 SECURITY CHECK: Is the email verified?
                if (repository.isEmailVerified()) {
                    val role = result.getOrNull() ?: UserRole.CUSTOMER
                    _authState.value = AuthState.Success(role)
                } else {
                    // If not verified
                    // V2 UX: They just logged in (maybe after a password reset).
                    // Let's send a fresh verification link automatically!
                    repository.sendEmailVerification()
                    repository.logout()

                    // Route them to the verification screen
                    _authState.value = AuthState.AwaitingVerification
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _authState.value = AuthState.PasswordResetSent
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        repository.logout()
        resetState()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}