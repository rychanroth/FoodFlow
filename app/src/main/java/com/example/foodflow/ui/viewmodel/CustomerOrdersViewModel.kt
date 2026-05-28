package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.CustomerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerOrdersViewModel : ViewModel() {

    private val repository = CustomerRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private var loadJob: Job? = null

    fun loadOrders(customerId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getOrdersForCustomer(customerId).collect { ordersList ->
                _orders.value = ordersList
            }
        }
    }

    fun clearOrders() {
        loadJob?.cancel()
        _orders.value = emptyList()
    }
}