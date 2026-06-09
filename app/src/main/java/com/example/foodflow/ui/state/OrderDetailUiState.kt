package com.example.foodflow.ui.state

import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.Order

data class OrderDetailUiState(
    val order: Order? = null,
    val customer: AppUser? = null,
    val restaurant: AppUser? = null,
    val driver: AppUser? = null,
    val isLoading: Boolean = true
)