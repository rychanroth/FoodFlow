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

    init {
        viewModelScope.launch {
            repository.getNewlyAddedItems().collect { items ->
                _newlyAddedItems.value = items
            }
        }
        viewModelScope.launch {
            repository.getRestaurants().collect { restaurants ->
                _restaurants.value = restaurants
            }
        }
    }
}
