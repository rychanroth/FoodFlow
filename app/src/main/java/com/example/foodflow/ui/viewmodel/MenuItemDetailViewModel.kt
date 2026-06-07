package com.example.foodflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.MenuRepository
import com.example.foodflow.data.repository.ProfileRepository
import com.example.foodflow.data.repository.UserPreferences
import com.example.foodflow.ui.state.MenuItemDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuItemDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val menuRepository = MenuRepository()
    private val profileRepository = ProfileRepository(application) // NEW
    private val userPreferences = UserPreferences(application)

    private val menuItemId: String = savedStateHandle["menuItemId"] ?: ""

    private val _state = MutableStateFlow(MenuItemDetailState())
    val state: StateFlow<MenuItemDetailState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                menuRepository.getMenuItemById(menuItemId),
                userPreferences.favoritesFlow
            ) { item, favoriteIds ->
                MenuItemDetailState(
                    item = item,
                    isFavorite = favoriteIds.contains(menuItemId),
                    isLoading = false
                )
            }.collect { state ->
                _state.value = state

                // NEW: Fetch restaurant data once we have the item and haven't fetched the restaurant yet
                if (state.item != null && _state.value.restaurant == null) {
                    fetchRestaurant(state.item.restaurantId)
                }
            }
        }
    }

    // NEW
    private fun fetchRestaurant(restaurantId: String) {
        viewModelScope.launch {
            val result = profileRepository.getUserProfile(restaurantId)
            if (result.isSuccess) {
                _state.update { it.copy(restaurant = result.getOrNull()) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            userPreferences.toggleFavorite(menuItemId)
        }
    }
}