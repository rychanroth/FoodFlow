package com.example.foodflow.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.MenuRepository
import com.example.foodflow.data.repository.ProfileRepository
import com.example.foodflow.ui.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository()
    private val menuRepository = MenuRepository() // Re-use ImgBB upload logic
    private val context = application

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val result = repository.getUserProfile(userId)
            if (result.isSuccess) {
                _profileState.value = ProfileState.Success(result.getOrNull()!!)
            } else {
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Failed to load profile")
            }
        }
    }

    fun updateUserName(userId: String, newName: String) {
        viewModelScope.launch {
            repository.updateUserProfile(userId, mapOf("uid" to newName)) // Simplified: just update a field
            loadUserProfile(userId) // Refresh data
        }
    }

    fun uploadAvatar(userId: String, imageUri: Uri) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val uploadResult = menuRepository.uploadImage(imageUri, context)
            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()!!
                repository.updateUserProfile(userId, mapOf("avatarUrl" to imageUrl))
                loadUserProfile(userId)
            } else {
                _profileState.value = ProfileState.Error("Failed to upload avatar")
            }
        }
    }
}