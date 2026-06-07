package com.example.foodflow.ui.state

import com.example.foodflow.data.model.MenuItem

data class MenuItemDetailState(
    val item: MenuItem? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true
)