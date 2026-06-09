package com.example.foodflow.ui.state

sealed interface RoleApplyState {
    data object Idle : RoleApplyState
    data object Loading : RoleApplyState
    data object Success : RoleApplyState
    data class Error(val message: String) : RoleApplyState
}