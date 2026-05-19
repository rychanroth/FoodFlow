package com.example.foodflow.ui.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<String>("idle")
    val authState: StateFlow<String> = _authState.asStateFlow()

    fun register(email: String, password: String, role: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value =  "error_empty_fields"
            return
        }

        _authState.value = "loading"

        viewModelScope.launch {
            val result = repository.registerUser(email, password, role)

            if (result.isSuccess) {
                _authState.value = "success"
            } else {
                _authState.value = "error: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}