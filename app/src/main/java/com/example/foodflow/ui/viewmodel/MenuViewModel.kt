package com.example.foodflow.ui.viewmodel

import android.net.Uri // ADD THIS IMPORT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    private val repository = MenuRepository()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val currentUserId: String?
        get() = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    init {
        currentUserId?.let { restaurantId ->
            viewModelScope.launch {
                repository.getMenuItems(restaurantId).collect { items ->
                    _menuItems.value = items
                }
            }
        }
    }

    fun addNewItem(name: String, description: String, price: Double, imageUri: Uri?) {
        val restaurantId = currentUserId ?: return
        viewModelScope.launch {
            // 1. Upload image if provided
            val imageUrl = imageUri?.let {
                repository.uploadImage(it).getOrNull() // Get URL if success, null if fail
            } ?: "" // Default to empty string if no image

            // 2. Create and save the menu item
            val newItem = MenuItem(
                restaurantId = restaurantId,
                name = name,
                description = description,
                price = price,
                imageUrl = imageUrl
            )
            repository.addMenuItem(newItem)
        }
    }


    fun updateItem(updatedItem: MenuItem, newImageUri: Uri?) {
        viewModelScope.launch {
            // If user picked a new image, upload it. Otherwise, keep the existing URL.
            val finalImageUrl = newImageUri?.let {
                repository.uploadImage(it).getOrNull()
            } ?: updatedItem.imageUrl

            val itemToSave = updatedItem.copy(imageUrl = finalImageUrl)
            repository.updateMenuItem(itemToSave)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }
}