package com.example.foodflow.ui.state

sealed interface SubmitPromoState {
    data object Idle : SubmitPromoState
    data object Loading : SubmitPromoState
    data object Success : SubmitPromoState
    data class Error(val message: String) : SubmitPromoState
}