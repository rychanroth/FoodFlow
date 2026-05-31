package com.example.foodflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.ThemePreference
import com.example.foodflow.data.repository.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    val themePreferenceState: StateFlow<ThemePreference> = userPreferences.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemePreference.SYSTEM)

    val dynamicColorState: StateFlow<Boolean> = userPreferences.dynamicColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setThemePreference(preference: ThemePreference) {
        viewModelScope.launch { userPreferences.setThemePreference(preference) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setDynamicColor(enabled) }
    }
}