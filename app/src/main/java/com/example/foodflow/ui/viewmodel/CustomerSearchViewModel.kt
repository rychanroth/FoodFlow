package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CustomerSearchViewModel : ViewModel() {

    private val repository = CustomerRepository()

    // Raw Data
    private val _allItems = MutableStateFlow<List<MenuItem>>(emptyList())
    private val _allRestaurants = MutableStateFlow<List<AppUser>>(emptyList())

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered Results for UI
    private val _filteredItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val filteredItems: StateFlow<List<MenuItem>> = _filteredItems.asStateFlow()

    private val _filteredRestaurants = MutableStateFlow<List<AppUser>>(emptyList())
    val filteredRestaurants: StateFlow<List<AppUser>> = _filteredRestaurants.asStateFlow()

    init {
        // Fetch raw data
        viewModelScope.launch { repository.getNewlyAddedItems().collect { _allItems.value = it } }
        viewModelScope.launch { repository.getRestaurants().collect { _allRestaurants.value = it } }

        // Filter items based on query
        viewModelScope.launch {
            combine(_allItems, _searchQuery) { items, query ->
                if (query.isBlank()) emptyList() // Don't show results until they type
                else items.filter { it.name.contains(query, ignoreCase = true) }
            }.collect { _filteredItems.value = it }
        }

        // Filter restaurants based on query
        viewModelScope.launch {
            combine(_allRestaurants, _searchQuery) { restaurants, query ->
                if (query.isBlank()) emptyList()
                else restaurants.filter { it.email.contains(query, ignoreCase = true) }
            }.collect { _filteredRestaurants.value = it }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}