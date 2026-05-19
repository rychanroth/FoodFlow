package com.example.foodflow.data.model

sealed interface AuthState {
    data object Idle: AuthState
    data object Loading: AuthState
    data class Success(val role: UserRole): AuthState
    data class Error(val message: String): AuthState
}