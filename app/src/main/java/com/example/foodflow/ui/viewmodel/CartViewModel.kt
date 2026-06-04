package com.example.foodflow.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.CartItem
import com.example.foodflow.ui.state.CheckoutState
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.data.model.PaymentMethod
import com.example.foodflow.data.model.PlatformSettings
import com.example.foodflow.data.repository.ConfigRepository
import com.example.foodflow.data.repository.CustomerRepository
import com.example.foodflow.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val repository = CustomerRepository()
    private val configRepository = ConfigRepository()

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // ADD: Expose settings so the UI can display the fee breakdown
    private val _settings = MutableStateFlow(PlatformSettings())
    val settings: StateFlow<PlatformSettings> = _settings

    var lastPaymentMethod: PaymentMethod = PaymentMethod.COD
        private set
    var lastOrderTotal = 0.0
    var lastOrderId: String? = null

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            val result = configRepository.getPlatformSettings()
            if (result.isSuccess) {
                _settings.value = result.getOrNull() ?: PlatformSettings()
            }
        }
    }

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

    fun placeOrder(currentUserId: String, paymentMethod: PaymentMethod) {
        val currentItems = _cartItems.value
        if (currentItems.isEmpty()) return

        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading // Use new state

            // 1. Fetch Live Platform Settings
            // Refactor: use the init block settings state
            val settings = _settings.value

            // 2. Calculate the Economics
            val subtotal = getTotalPrice()
            val deliveryFee = settings.deliveryFee
            val platformFee = settings.platformFlatFee
            val totalAmount = subtotal + deliveryFee + platformFee

            val restaurantEarnings = subtotal - (subtotal * settings.platformCommissionRate)
            val driverEarnings = deliveryFee * settings.driverCommissionRate
            val platformEarnings = (subtotal * settings.platformCommissionRate) + (deliveryFee * (1 - settings.driverCommissionRate)) + platformFee

            val restaurantId = currentItems.first().menuItem.restaurantId
            val itemNames = currentItems.map { it.menuItem.name }

            // Update state
            lastPaymentMethod = paymentMethod
            lastOrderTotal = totalAmount

            // V2 LOGIC: Set status based on payment method
            val initialStatus = if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
                OrderStatus.PENDING_PAYMENT_VERIFICATION
            } else {
                OrderStatus.PLACED
            }

            val newOrder = Order(
                customerId = currentUserId,
                restaurantId = restaurantId,
                itemNames = itemNames,
                status = initialStatus,
                paymentMethod = paymentMethod,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                platformFee = platformFee,
                totalAmount = totalAmount,
                restaurantEarnings = restaurantEarnings,
                driverEarnings = driverEarnings,
                platformEarnings = platformEarnings
            )

            // 4. Save to Firestore
            val result = repository.placeOrder(newOrder)
            if (result.isSuccess) {
                lastOrderId = result.getOrNull()
                clearCart()
                _checkoutState.value = CheckoutState.Success // Use new state
            } else {
                _checkoutState.value = CheckoutState.Error(result.exceptionOrNull()?.message ?: "Order failed") // Use new state
            }
        }
    }

    // ADD: Function to upload the transaction proof
    fun uploadTransactionProof(orderId: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            val uploadResult =
                MenuRepository().uploadImage(imageUri, context) // Re-use our ImgBB repo!
            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()!!
                // Update the order in Firestore with the image URL
                repository.updateOrderTransactionProof(orderId, imageUrl)
                _checkoutState.value = CheckoutState.Success
            } else {
                _checkoutState.update { CheckoutState.Error("Failed to upload receipt. Please try again.") }
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }
}
