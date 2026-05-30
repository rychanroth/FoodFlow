package com.example.foodflow.ui.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun register(email: String, password: String, role: UserRole) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Fields cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.registerUser(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(role)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
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
                val role = result.getOrNull() ?: UserRole.CUSTOMER
                _authState.value = AuthState.Success(role)
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