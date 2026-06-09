package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.data.repository.CustomerRepository
import com.example.foodflow.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerHomeViewModel : ViewModel() {

    private val customerRepository = CustomerRepository()
    private val menuRepository = MenuRepository() // NEW

    private val _newlyAddedItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val newlyAddedItems: StateFlow<List<MenuItem>> = _newlyAddedItems.asStateFlow()

    private val _restaurants = MutableStateFlow<List<AppUser>>(emptyList())
    val restaurants: StateFlow<List<AppUser>> = _restaurants.asStateFlow()

    // NEW V3: Categories & Promotions
    private val _categories = MutableStateFlow<List<MenuItemCategory>>(emptyList())
    val categories: StateFlow<List<MenuItemCategory>> = _categories.asStateFlow()

    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            menuRepository.getNewlyAddedItems().collect { _newlyAddedItems.value = it; _isLoading.value = false }
        }
        viewModelScope.launch {
            customerRepository.getRestaurants().collect { _restaurants.value = it; _isLoading.value = false }
        }
        // NEW V3:
        viewModelScope.launch {
            menuRepository.getCategories().collect { _categories.value = it }
        }
        viewModelScope.launch {
            menuRepository.getActivePromotions().collect { _promotions.value = it }
        }
    }
}