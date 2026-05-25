package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.CartItem
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val repository = CustomerRepository()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addItemToCart(item: MenuItem) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.menuItem.id == item.id }
        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentItems.add(CartItem(menuItem = item, quantity = 1))
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(menuItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.menuItem.id != menuItemId }
    }

    fun increaseQuantity(menuItemId: String) {
        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.menuItem.id == menuItemId }
        if (index != -1) {
            val item = currentItems[index]
            currentItems[index] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = currentItems
        }
    }

    fun decreaseQuantity(menuItemId: String) {
        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.menuItem.id == menuItemId }
        if (index != -1) {
            val item = currentItems[index]
            if (item.quantity > 1) {
                currentItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentItems.removeAt(index)
            }
            _cartItems.value = currentItems
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf { it.menuItem.price * it.quantity }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder(currentUserId: String) {
        val currentItems = _cartItems.value
        if (currentItems.isEmpty()) return

        viewModelScope.launch {
            val restaurantId = currentItems.first().menuItem.restaurantId
            val newOrder = Order(
                customerId = currentUserId,
                restaurantId = restaurantId,
                items = currentItems,
                totalAmount = getTotalPrice()
            )
            val result = repository.placeOrder(newOrder)
            if (result.isSuccess) {
                clearCart()
            }
        }
    }
}
