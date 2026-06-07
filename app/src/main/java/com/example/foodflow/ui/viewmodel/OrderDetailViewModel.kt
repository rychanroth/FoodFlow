package com.example.foodflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.OrderRepository
import com.example.foodflow.data.repository.ProfileRepository
import com.example.foodflow.ui.state.OrderDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val orderRepository = OrderRepository()
    private val profileRepository = ProfileRepository(application)

    private val orderId: String = savedStateHandle.get<String>("orderId") ?: ""

    private val _state = MutableStateFlow(OrderDetailUiState())
    val state: StateFlow<OrderDetailUiState> = _state.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            orderRepository.getOrderStream(orderId).collect { order ->
                if (order != null) {
                    _state.update { it.copy(order = order, isLoading = false) }
                    fetchUsers(order) // Fetch related users
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun fetchUsers(order: Order) {
        viewModelScope.launch {
            // Only fetch if we don't have them yet
            if (_state.value.customer == null && order.customerId.isNotEmpty()) {
                val result = profileRepository.getUserProfile(order.customerId)
                if (result.isSuccess) _state.update { it.copy(customer = result.getOrNull()) }
            }
            if (_state.value.restaurant == null && order.restaurantId.isNotEmpty()) {
                val result = profileRepository.getUserProfile(order.restaurantId)
                if (result.isSuccess) _state.update { it.copy(restaurant = result.getOrNull()) }
            }
            if (_state.value.driver == null && order.driverId != null) {
                val result = profileRepository.getUserProfile(order.driverId!!)
                if (result.isSuccess) _state.update { it.copy(driver = result.getOrNull()) }
            }
        }
    }
}