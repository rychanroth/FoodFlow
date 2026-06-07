package com.example.foodflow.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.data.repository.ImageUploader
import com.example.foodflow.data.repository.MenuRepository
import com.example.foodflow.data.repository.OrderRepository
import com.example.foodflow.ui.state.SubmitPromoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestaurantDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository()
    private val menuRepository = MenuRepository()

    private val _todaysOrders = MutableStateFlow<List<Order>>(emptyList())
    val todaysOrders: StateFlow<List<Order>> = _todaysOrders.asStateFlow()

    // Derived states for the UI
    private val _todaysOrderCount = MutableStateFlow(0)
    val todaysOrderCount: StateFlow<Int> = _todaysOrderCount.asStateFlow()

    private val _todaysRevenue = MutableStateFlow(0.0)
    val todaysRevenue: StateFlow<Double> = _todaysRevenue.asStateFlow()

    private val _pendingOrdersCount = MutableStateFlow(0)
    val pendingOrdersCount: StateFlow<Int> = _pendingOrdersCount.asStateFlow()
    private val _promoState = MutableStateFlow<SubmitPromoState>(SubmitPromoState.Idle)
    val promoState: StateFlow<SubmitPromoState> = _promoState.asStateFlow()

    // NEW V3: Fetch this restaurant's menu items for the dialog dropdown
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    fun loadMenuItems(restaurantId: String) {
        viewModelScope.launch {
            menuRepository.getMenuItems(restaurantId).collect { _menuItems.value = it }
        }
    }

    // Updated to include menuItemId
    fun submitPromotion(restaurantId: String, menuItemId: String, imageUri: Uri) {
        viewModelScope.launch {
            _promoState.value = SubmitPromoState.Loading

            val uploadResult = ImageUploader.upload(imageUri, getApplication())

            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()!!
                val newPromotion = Promotion(
                    restaurantId = restaurantId,
                    menuItemId = menuItemId, // Save the linked item
                    imageUrl = imageUrl,
                    isActive = false
                )
                menuRepository.addPromotion(newPromotion)
                _promoState.value = SubmitPromoState.Success
            } else {
                _promoState.value = SubmitPromoState.Error("Failed to upload banner image")
            }
        }
    }

    fun resetPromoState() {
        _promoState.value = SubmitPromoState.Idle
    }

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