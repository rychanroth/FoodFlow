package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.MenuRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    private val repository = MenuRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // State for the list of menu items
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    init {
        // For MVP, we are assuming the logged-in Restaurant's UID is their restaurantId.
        // We will fetch the actual UID from Firebase Auth.
        val currentRestaurantId = getCurrentUserId()
        if (currentRestaurantId != null) {
            // Start listening to the menu items in real-time
            viewModelScope.launch {
                repository.getMenuItems(currentRestaurantId).collect { items ->
                    _menuItems.value = items
                }
            }
        }
    }

    // Helper to get current user ID
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun addNewItem(name: String, description: String, price: Double) {
        val restaurantId = getCurrentUserId() ?: return
        viewModelScope.launch {
            val newItem = MenuItem(
                restaurantId = restaurantId,
                name = name,
                description = description,
                price = price
            )
            repository.addMenuItem(newItem)
        }
    }

    // Update an item
    fun updateItem(updatedItem: MenuItem) {
        viewModelScope.launch {
            repository.updateMenuItem(updatedItem)
        }
    }

    // Delete an item
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }
}