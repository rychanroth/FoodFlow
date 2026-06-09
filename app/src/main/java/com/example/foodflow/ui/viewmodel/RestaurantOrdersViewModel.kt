package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RestaurantOrdersViewModel : ViewModel() {
    private val repository = OrderRepository()

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())

    // NEW: Selected status filter (null means "All")
    private val _selectedStatus = MutableStateFlow<OrderStatus?>(null)
    val selectedStatus: StateFlow<OrderStatus?> = _selectedStatus.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // NEW: Filtered list based on selected status
    val filteredOrders = combine(_allOrders, _selectedStatus) { orders, status ->
        if (status == null) {
            orders
        } else {
            orders.filter { it.status == status }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadOrders(restaurantId: String) {
        viewModelScope.launch {
            repository.getOrdersForThisRestaurant(restaurantId).collect { ordersList ->
                _allOrders.value = ordersList
            }
        }
    }

    // NEW
    fun selectStatus(status: OrderStatus?) {
        _selectedStatus.value = status
    }

    fun verifyBankPayment(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.PLACED)
        }
    }

    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.PREPARING) // Changed
        }
    }

    fun rejectOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.REJECTED) // Changed
        }
    }

    fun markReadyForPickup(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.READY) // Changed
        }
    }

}