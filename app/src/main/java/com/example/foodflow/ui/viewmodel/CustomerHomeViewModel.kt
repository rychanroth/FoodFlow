package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerHomeViewModel : ViewModel() {

    private val repository = CustomerRepository()

    private val _newlyAddedItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val newlyAddedItems: StateFlow<List<MenuItem>> = _newlyAddedItems.asStateFlow()

    private val _restaurants = MutableStateFlow<List<AppUser>>(emptyList())
    val restaurants: StateFlow<List<AppUser>> = _restaurants.asStateFlow()

    // NEW: Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getNewlyAddedItems().collect {
                _newlyAddedItems.value = it
                _isLoading.value = false // Data arrived, stop loading!
            }
        }
        viewModelScope.launch {
            repository.getRestaurants().collect {
                _restaurants.value = it
                _isLoading.value = false // Data arrived, stop loading!
            }
        }
    }
}
