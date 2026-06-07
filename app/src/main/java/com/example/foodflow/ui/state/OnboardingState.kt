package com.example.foodflow.ui.state

sealed interface OnboardingState {
    data object Idle : OnboardingState
    data object Loading : OnboardingState
    data object Success : OnboardingState
    data class Error(val message: String) : OnboardingState
}