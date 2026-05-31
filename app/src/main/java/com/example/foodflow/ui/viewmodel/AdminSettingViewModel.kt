package com.example.foodflow.ui.viewmodel

import SettingsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.PlatformSettings
import com.example.foodflow.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminSettingsViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Loading)
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Loading
            val result = repository.getPlatformSettings()
            if (result.isSuccess) {
                _settingsState.value = SettingsState.Success(result.getOrNull() ?: PlatformSettings())
            } else {
                _settingsState.value = SettingsState.Error(result.exceptionOrNull()?.message ?: "Failed to load")
            }
        }
    }

    fun saveSettings(settings: PlatformSettings) {
        viewModelScope.launch {
            val result = repository.updatePlatformSettings(settings)
            if (result.isSuccess) {
                _settingsState.value = SettingsState.Saved
                // Reload to confirm
                loadSettings()
            } else {
                _settingsState.value = SettingsState.Error(result.exceptionOrNull()?.message ?: "Failed to save")
            }
        }
    }
}