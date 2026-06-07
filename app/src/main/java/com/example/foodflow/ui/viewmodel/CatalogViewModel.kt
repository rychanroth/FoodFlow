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

package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    // Fetch items dynamically based on selected category
    // If no category is selected, we could fetch all, but for a catalog, it's better to require a selection
    // or default to the first category to save Firestore reads. Here we'll fetch based on selection.
    private val _items = MutableStateFlow<List<MenuItem>>(emptyList())
    val items: StateFlow<List<MenuItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Observe category selection and fetch items accordingly
        viewModelScope.launch {
            _selectedCategoryId.collect { categoryId ->
                if (categoryId.isNotEmpty()) {
                    repository.getMenuItemsByCategory(categoryId).collect { list ->
                        _items.value = list
                        _isLoading.value = false
                    }
                } else {
                    // If no category selected (e.g., coming from "See All" with no specific ID)
                    // We can fetch newly added items as a default, or leave empty.
                    // Let's fetch newly added items as a default browse view.
                    repository.getNewlyAddedItems(limit = 20).collect { list ->
                        _items.value = list
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _isLoading.value = true
        _selectedCategoryId.value = categoryId
    }
}