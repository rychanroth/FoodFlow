package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = OrderRepository()

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    init {
        // Extract the orderId from the navigation arguments
        val orderId = savedStateHandle.get<String>("orderId") ?: ""
        if (orderId.isNotEmpty()) {
            viewModelScope.launch {
                repository.getOrderStream(orderId).collect { fetchedOrder ->
                    _order.value = fetchedOrder
                }
            }
        }
    }
}