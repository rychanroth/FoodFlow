package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class estaurantDashboardViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _todaysOrders = MutableStateFlow<List<Order>>(emptyList())
    val todaysOrders: StateFlow<List<Order>> = _todaysOrders.asStateFlow()

    // Derived states for the UI
    private val _todaysOrderCount = MutableStateFlow(0)
    val todaysOrderCount: StateFlow<Int> = _todaysOrderCount.asStateFlow()

    private val _todaysRevenue = MutableStateFlow(0.0)
    val todaysRevenue: StateFlow<Double> = _todaysRevenue.asStateFlow()

    private val _pendingOrdersCount = MutableStateFlow(0)
    val pendingOrdersCount: StateFlow<Int> = _pendingOrdersCount.asStateFlow()

    fun loadDashboard(restaurantId: String?) {
        viewModelScope.launch {
            repository.getThisRestaurantOrdersForToday(restaurantId).collect { orders ->
                _todaysOrders.value = orders

                // Local aggregation
                _todaysOrderCount.value = orders.size
                _todaysRevenue.value = orders.sumOf { it.restaurantEarnings }
                _pendingOrdersCount.value = orders.count {
                    it.status == OrderStatus.PLACED || it.status == OrderStatus.PENDING_PAYMENT_VERIFICATION
                }
            }
        }
    }
}