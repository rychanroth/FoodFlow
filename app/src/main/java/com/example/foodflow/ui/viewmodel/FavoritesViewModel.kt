package com.example.foodflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.MenuRepository
import com.example.foodflow.data.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val menuRepository = MenuRepository()
    private val userPreferences = UserPreferences(application)

    private val _favoriteItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val favoriteItems: StateFlow<List<MenuItem>> = _favoriteItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Whenever DataStore favorites change, flatMapLatest triggers a new Firestore fetch
        viewModelScope.launch {
            userPreferences.favoritesFlow.flatMapLatest { favoriteIds ->
                _isLoading.value = true
                // Suspend function converting the flow to a single emission
                kotlinx.coroutines.flow.flow {
                    val result = menuRepository.getMenuItemsByIds(favoriteIds.toList())
                    emit(result.getOrDefault(emptyList()))
                }
            }.collect { items ->
                _favoriteItems.value = items
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(itemId: String) {
        viewModelScope.launch {
            userPreferences.toggleFavorite(itemId)
        }
    }
}