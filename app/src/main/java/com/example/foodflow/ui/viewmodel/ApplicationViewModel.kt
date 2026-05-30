package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.data.repository.ApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State for the Apply screen
sealed interface ApplyState {
    data object Idle : ApplyState
    data object Loading : ApplyState
    data object Success : ApplyState
    data class Error(val message: String) : ApplyState
}

class ApplicationViewModel : ViewModel() {

    private val repository = ApplicationRepository()

    private val _applyState = MutableStateFlow<ApplyState>(ApplyState.Idle)
    val applyState: StateFlow<ApplyState> = _applyState.asStateFlow()

    fun submitApplication(userId: String, userEmail: String, requestedRole: UserRole, businessDetails: String) {
        if (businessDetails.isBlank()) {
            _applyState.value = ApplyState.Error("Please provide business details")
            return
        }

        _applyState.value = ApplyState.Loading

        viewModelScope.launch {
            val result = repository.submitApplication(userId, userEmail, requestedRole, businessDetails)
            if (result.isSuccess) {
                _applyState.value = ApplyState.Success
            } else {
                _applyState.value = ApplyState.Error(result.exceptionOrNull()?.message ?: "Submission failed")
            }
        }
    }

    fun resetState() {
        _applyState.value = ApplyState.Idle
    }
}