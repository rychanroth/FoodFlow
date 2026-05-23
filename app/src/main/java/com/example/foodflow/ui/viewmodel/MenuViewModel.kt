package com.example.foodflow.ui.viewmodel

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
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val context = application

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    init {
        currentUserId?.let { uid ->
            viewModelScope.launch {
                repository.getMenuItems(uid).collect { items ->
                    _menuItems.value = items
                }
            }
        }
    }

    fun addNewItem(name: String, description: String, price: Double, imageUri: Uri?) {
        val restaurantId = currentUserId ?: return
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

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }
}