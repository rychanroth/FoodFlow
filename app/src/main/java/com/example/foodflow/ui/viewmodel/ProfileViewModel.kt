package com.example.foodflow.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.ImageUploader
import com.example.foodflow.data.repository.ProfileRepository
import com.example.foodflow.ui.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val result = repository.getUserProfile(userId)
            if (result.isSuccess) {
                _profileState.value = ProfileState.Success(result.getOrNull()!!)
            } else {
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Failed to load profile")
            }
        }
    }

    fun updateUserProfile(userId: String, name: String, phone: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            val updates = mapOf(
                "name" to name.trim(),
                "phone" to phone.trim()
            )
            repository.updateUserProfile(userId, updates)
            loadUserProfile(userId) // Refresh data
            _isUpdating.value = false
        }
    }

    fun uploadAvatar(userId: String, imageUri: Uri) {
        viewModelScope.launch {
            _isUpdating.value = true
            val uploadResult = ImageUploader.upload(imageUri, getApplication<Application>())
            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()!!
                repository.updateUserProfile(userId, mapOf("avatarUrl" to imageUrl))
                loadUserProfile(userId)
            }
            _isUpdating.value = false
        }
    }
}