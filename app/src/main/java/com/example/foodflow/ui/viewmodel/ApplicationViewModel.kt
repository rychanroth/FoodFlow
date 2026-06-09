package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.data.repository.RoleApplicationRepository
import com.example.foodflow.ui.state.RoleApplyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationViewModel : ViewModel() {

    private val repository = RoleApplicationRepository()

    private val _applyState = MutableStateFlow<RoleApplyState>(RoleApplyState.Idle)
    val applyState: StateFlow<RoleApplyState> = _applyState.asStateFlow()

    fun submitApplication(userId: String, userEmail: String, requestedRole: UserRole, businessDetails: String) {
        if (businessDetails.isBlank()) {
            _applyState.value = RoleApplyState.Error("Please provide business details")
            return
        }

        _applyState.value = RoleApplyState.Loading

        viewModelScope.launch {
            val result = repository.submitApplication(userId, userEmail, requestedRole, businessDetails)
            if (result.isSuccess) {
                _applyState.value = RoleApplyState.Success
            } else {
                _applyState.value = RoleApplyState.Error(result.exceptionOrNull()?.message ?: "Submission failed")
            }
        }
    }

    fun resetState() {
        _applyState.value = RoleApplyState.Idle
    }
}