package com.example.foodflow.ui.state

sealed interface ApplyState {
    data object Idle : ApplyState
    data object Loading : ApplyState
    data object Success : ApplyState
    data class Error(val message: String) : ApplyState
}