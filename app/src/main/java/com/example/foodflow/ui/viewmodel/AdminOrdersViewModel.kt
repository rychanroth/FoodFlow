package com.example.foodflow.ui.viewmodel

import kotlinx.coroutines.flow.asStateFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AdminOrdersViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            // Fetch last 30 days of platform orders
            val endTime = Calendar.getInstance().timeInMillis

            val startTime = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -30)
            }.timeInMillis

            repository.getPlatformOrders(startTime, endTime).collect { ordersList ->
                _orders.value = ordersList
            }
        }
    }
}