package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriverOrdersViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _availableOrders = MutableStateFlow<List<Order>>(emptyList())
    val availableOrders: StateFlow<List<Order>> = _availableOrders.asStateFlow()

    private val _myActiveDeliveries = MutableStateFlow<List<Order>>(emptyList())
    val myActiveDeliveries: StateFlow<List<Order>> = _myActiveDeliveries.asStateFlow()

    fun loadAvailableOrders() {
        viewModelScope.launch {
            repository.getAvailableOrders().collect { _availableOrders.value = it }
        }
    }

    fun loadMyActiveDeliveries(driverId: String) {
        viewModelScope.launch {
            repository.getMyActiveDeliveries(driverId).collect { _myActiveDeliveries.value = it }
        }
    }

    fun acceptOrder(orderId: String, driverId: String) {
        viewModelScope.launch {
            repository.acceptOrder(orderId, driverId)
        }
    }

    fun markAsDelivered(orderId: String) {
        viewModelScope.launch {
            repository.markAsDelivered(orderId)
        }
    }
}