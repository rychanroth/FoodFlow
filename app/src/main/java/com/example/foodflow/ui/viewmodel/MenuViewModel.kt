package com.example.foodflow.ui.viewmodel

import kotlinx.coroutines.Job
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.repository.MenuRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MenuRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val context = application
    private var loadJob: Job? = null

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    // FIX: Instead of init block not loading because of bad state of currentUserId
    // NEW: Call this manually when the screen loads
    fun loadMenuItems(restaurantId: String) {
        // Cancel previous listener if it exists (prevents duplicates)
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            repository.getMenuItems(restaurantId).collect { items ->
                _menuItems.value = items
            }
        }
    }

    // FIX: Instead of initializing currentUserId right away (which has some firebase auth instance initialization incompat,
    // We have a Helper to get current user ID for adding/updating items
    private fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun addNewMenuItem(name: String, description: String, price: Double, imageUri: Uri?) {
        val restaurantId = getCurrentUserId() ?: return
        viewModelScope.launch {
            val imageUrl = imageUri?.let {
                repository.uploadImage(it, context).getOrNull()
            } ?: ""

            val newItem = MenuItem(
                restaurantId = restaurantId,
                name = name,
                description = description,
                price = price,
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis()
            )
            repository.addMenuItem(newItem)
        }
    }

    fun updateMenuItem(updatedItem: MenuItem, newImageUri: Uri?) {
        viewModelScope.launch {
            // If user picked a new image, upload it. Otherwise, keep the existing URL.
            val finalImageUrl = newImageUri?.let {
                repository.uploadImage(it, context).getOrNull()
            } ?: updatedItem.imageUrl

            val itemToSave = updatedItem.copy(imageUrl = finalImageUrl)
            repository.updateMenuItem(itemToSave)
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }

    // Clear list on logout
    fun clearMenu() {
        loadJob?.cancel()
        _menuItems.value = emptyList()
    }
}