package com.example.foodflow.ui.state

sealed interface CheckoutState {
    data object Idle : CheckoutState
    data object Loading : CheckoutState
    data object Success : CheckoutState
    data class Error(val message: String) : CheckoutState
}