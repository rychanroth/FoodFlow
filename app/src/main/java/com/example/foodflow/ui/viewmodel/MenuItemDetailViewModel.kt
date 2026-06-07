package com.example.foodflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.MenuRepository
import com.example.foodflow.data.repository.UserPreferences
import com.example.foodflow.ui.state.MenuItemDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MenuItemDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val menuRepository = MenuRepository()
    private val userPreferences = UserPreferences(application)

    // Automatically extract the menuItemId from the navigation arguments
    private val menuItemId: String = savedStateHandle["menuItemId"] ?: ""

    private val _state = MutableStateFlow(MenuItemDetailState())
    val state: StateFlow<MenuItemDetailState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Combine the Firestore item flow and the DataStore favorites flow
            combine(
                menuRepository.getMenuItemById(menuItemId),
                userPreferences.favoritesFlow
            ) { item, favoriteIds ->
                MenuItemDetailState(
                    item = item,
                    isFavorite = favoriteIds.contains(menuItemId),
                    isLoading = false
                )
            }.collect { _state.value = it }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            userPreferences.toggleFavorite(menuItemId)
        }
    }
}