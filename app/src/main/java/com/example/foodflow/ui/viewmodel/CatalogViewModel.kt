package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class CatalogViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = MenuRepository()

    // Extract optional initial categoryId from Navigation arguments
    private val initialCategoryId: String = savedStateHandle["categoryId"] ?: ""
    val categoryName: String = savedStateHandle["categoryName"] ?: ""

    private val _selectedCategoryId = MutableStateFlow(initialCategoryId)
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    // Fetch all categories for the filter chips
    val categories = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ✅ FIX: Use flatMapLatest to react to category changes without blocking
    val items = _selectedCategoryId
        .onEach { _isLoading.value = true } // Show spinner when category changes
        .flatMapLatest { categoryId ->
            if (categoryId.isNotEmpty()) {
                repository.getMenuItemsByCategory(categoryId)
            } else {
                repository.getNewlyAddedItems(limit = 20)
            }
        }
        .onEach { _isLoading.value = false } // Hide spinner once data emits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }
}