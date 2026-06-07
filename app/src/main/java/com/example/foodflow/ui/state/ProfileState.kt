package com.example.foodflow.ui.state

import com.example.foodflow.data.model.AppUser

sealed interface ProfileState {
    data object Loading : ProfileState
    data class Success(val user: AppUser) : ProfileState
    data class Error(val message: String) : ProfileState
}