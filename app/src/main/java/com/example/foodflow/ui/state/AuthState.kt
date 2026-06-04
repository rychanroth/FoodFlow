package com.example.foodflow.ui.state

import com.example.foodflow.data.model.UserRole

sealed interface AuthState {
    data object Idle: AuthState
    data object Loading: AuthState
    data class Success(val role: UserRole): AuthState
    data class Error(val message: String): AuthState

    data object PasswordResetSent: AuthState
    data object AwaitingVerification: AuthState
}