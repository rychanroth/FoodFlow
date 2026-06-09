package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.data.repository.AuthRepository
import com.example.foodflow.ui.state.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    init {
        val uid = repository.currentUserId
        if (uid != null) {
            // FIX: Set to Loading immediately so the UI shows a spinner instead of
            // flashing the Login screen while waiting for Firestore data.
            _authState.value = AuthState.Loading

            viewModelScope.launch {
                val result = repository.getCurrentAppUser(uid)
                if (result.isSuccess) {
                    _currentUser.value = result.getOrNull()
                    _authState.value = AuthState.Success(_currentUser.value?.role ?: UserRole.CUSTOMER)
                } else {
                    // If fetching profile fails, drop back to Idle (Login screen)
                    _authState.value = AuthState.Idle
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
            val regResult = repository.registerUser(email, password)

            if (regResult.isSuccess) {
                _currentUser.value = regResult.getOrNull() // SET USER
                val verifyResult = repository.sendEmailVerification()
                if (verifyResult.isSuccess) {
                    repository.logout()
                    _currentUser.value = null // CLEAR USER
                    _authState.value = AuthState.AwaitingVerification
                } else {
                    _authState.value = AuthState.Error("Registered, but failed to send verification email.")
                }
            } else {
                val errorMsg = regResult.exceptionOrNull()?.message ?: "Unknown error"
                if (errorMsg.contains("already in use", ignoreCase = true)) {
                    _authState.value = AuthState.Error("This email is already registered. Try logging in.")
                } else {
                    _authState.value = AuthState.Error(errorMsg)
                }
            }
        }
    }

    fun googleSignIn(idToken: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.firebaseAuthWithGoogle(idToken)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull() // SET USER
                _authState.value = AuthState.Success(_currentUser.value?.role ?: UserRole.CUSTOMER)
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
                if (repository.isEmailVerified()) {
                    val uid = repository.currentUserId ?: return@launch
                    val userResult = repository.getCurrentAppUser(uid)
                    if (userResult.isSuccess) {
                        _currentUser.value = userResult.getOrNull() // SET USER
                        _authState.value = AuthState.Success(_currentUser.value?.role ?: UserRole.CUSTOMER)
                    } else {
                        _authState.value = AuthState.Error("Failed to fetch user data")
                    }
                } else {
                    repository.sendEmailVerification()
                    repository.logout()
                    _currentUser.value = null // CLEAR USER
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

    suspend fun refreshCurrentUser(): AppUser? {
        val uid = repository.currentUserId ?: return null
        val result = repository.getCurrentAppUser(uid)
        if (result.isSuccess) {
            val user = result.getOrNull()
            _currentUser.value = user
            return user
        }
        return null
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null // CLEAR USER
        resetState()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}