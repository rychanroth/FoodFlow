package com.example.foodflow.ui.state

import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem

data class MenuItemDetailState(
    val item: MenuItem? = null,
    val restaurant: AppUser? = null, // NEW
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true
)