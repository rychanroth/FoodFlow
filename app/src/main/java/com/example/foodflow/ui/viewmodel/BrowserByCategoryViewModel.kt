package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrowseByCategoryViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = MenuRepository()

    // Extract categoryId from Navigation arguments
    private val categoryId: String = savedStateHandle["categoryId"] ?: ""
    val categoryName: String = savedStateHandle["categoryName"] ?: ""

    private val _items = MutableStateFlow<List<MenuItem>>(emptyList())
    val items: StateFlow<List<MenuItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            repository.getMenuItemsByCategory(categoryId).collect { list ->
                _items.value = list
                _isLoading.value = false
            }
        }
    }
}