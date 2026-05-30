package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Application
import com.example.foodflow.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _pendingApps = MutableStateFlow<List<Application>>(emptyList())
    val pendingApps: StateFlow<List<Application>> = _pendingApps.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPendingApplications().collect { apps ->
                _pendingApps.value = apps
            }
        }
    }

    fun approveApplication(app: Application) {
        viewModelScope.launch {
            repository.approveApplication(app.id, app.userId, app.requestedRole.name)
        }
    }

    fun rejectApplication(appId: String) {
        viewModelScope.launch {
            repository.rejectApplication(appId)
        }
    }
}