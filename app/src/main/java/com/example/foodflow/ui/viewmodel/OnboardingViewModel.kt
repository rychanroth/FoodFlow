package com.example.foodflow.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.AuthRepository
import com.example.foodflow.data.repository.ProfileRepository
import com.example.foodflow.ui.state.OnboardingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val profileRepository = ProfileRepository(getApplication())
    private val authRepository = AuthRepository()

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
    val onboardingState: StateFlow<OnboardingState> = _onboardingState.asStateFlow()

    fun completeOnboarding(name: String, avatarUri: Uri?, context: Context) {
        viewModelScope.launch {
            _onboardingState.value = OnboardingState.Loading

            val uid = authRepository.currentUserId ?: run {
                _onboardingState.value = OnboardingState.Error("Not authenticated")
                return@launch
            }

            val avatarUrl = if (avatarUri != null) {
                profileRepository.uploadImage(avatarUri, context).getOrNull() ?: ""
            } else {
                ""
            }

            val updates = mapOf(
                "name" to name.trim(),
                "avatarUrl" to avatarUrl,
                "isProfileComplete" to true
            )

            val result = profileRepository.updateUserProfile(uid, updates)
            if (result.isSuccess) {
                _onboardingState.value = OnboardingState.Success
            } else {
                _onboardingState.value = OnboardingState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to save profile"
                )
            }
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            _onboardingState.value = OnboardingState.Loading
            val uid = authRepository.currentUserId ?: run {
                _onboardingState.value = OnboardingState.Error("Not authenticated")
                return@launch
            }
            val result = profileRepository.updateUserProfile(
                uid, mapOf("isProfileComplete" to true)
            )
            if (result.isSuccess) {
                _onboardingState.value = OnboardingState.Success
            } else {
                _onboardingState.value = OnboardingState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to skip"
                )
            }
        }
    }

    fun resetState() {
        _onboardingState.value = OnboardingState.Idle
    }
}