package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.data.model.CartItem
import com.example.foodflow.data.model.PaymentMethod
import com.example.foodflow.ui.components.common.OrderSummarySheet
import com.example.foodflow.ui.components.customer.CartItemCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.CheckoutState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val settings by cartViewModel.settings.collectAsState()
    val checkoutState by cartViewModel.checkoutState.collectAsState()
    val user by authViewModel.currentUser.collectAsState() // NEW

    val subtotal = cartViewModel.getTotalPrice()
    val totalPrice = subtotal + settings.deliveryFee + settings.platformFlatFee
    val currentUser = FirebaseAuth.getInstance().currentUser

    var showSummarySheet by remember { mutableStateOf(false) }
    var showMissingAddressDialog by remember { mutableStateOf(false) } // NEW

    // Check if user has a default address
    val hasValidAddress = user?.addresses?.any { it.isDefault } == true

    // NEW: Missing Address Dialog
    if (showMissingAddressDialog) {
        AlertDialog(
            onDismissRequest = { showMissingAddressDialog = false },
            title = { Text("Delivery Address Required") },
            text = { Text("Please add a default delivery address in your profile before placing an order.") },
            confirmButton = {
                Button(onClick = {
                    showMissingAddressDialog = false
                    navController.navigate(Route.Profile.route)
                }) {
                    Text("Go to Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMissingAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSummarySheet) {
        ModalBottomSheet(onDismissRequest = { showSummarySheet = false }) {
            OrderSummarySheet(
                cartItems = cartItems,
                settings = settings,
                onConfirmOrder = { paymentMethod ->
                    // VALIDATION GATE
                    if (!hasValidAddress) {
                        showSummarySheet = false // Close sheet
                        showMissingAddressDialog = true // Show dialog
                    } else {
                        if (currentUser != null) {
                            cartViewModel.placeOrder(currentUser.uid, paymentMethod)
                        }
                        showSummarySheet = false
                    }
                },
                onDismiss = { showSummarySheet = false }
            )
        }
    }

    CartContent(
        cartItems = cartItems,
        totalPrice = totalPrice,
        onBackClick = { navController.popBackStack() },
        onShowSummaryClick = { showSummarySheet = true },
        onIncreaseClick = { cartViewModel.increaseQuantity(it) },
        onDecreaseClick = { cartViewModel.decreaseQuantity(it) }
    )

    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutState.Success) {
            if (cartViewModel.lastPaymentMethod == PaymentMethod.BANK_TRANSFER) {
                navController.navigate(Route.PaymentInstruction.route)
            } else {
                navController.navigate(Route.CustomerHome.route) {
                    popUpTo(Route.CustomerHome.route) { inclusive = true }
                }
            }
            cartViewModel.resetCheckoutState()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    cartItems: List<CartItem>,
    totalPrice: Double,
    onBackClick: () -> Unit,
    onShowSummaryClick: () -> Unit,
    onIncreaseClick: (String) -> Unit,
    onDecreaseClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: $${"%.2f".format(totalPrice)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = onShowSummaryClick,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Show Summary",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Pro-Tip: Adding the key here stops the list from flickering!
                items(cartItems, key = { it.menuItem.id }) { cartItem ->
                    CartItemCard(
                        item = cartItem,
                        onIncreaseClick = { onIncreaseClick(cartItem.menuItem.id) },
                        onDecreaseClick = { onDecreaseClick(cartItem.menuItem.id) }
                    )
                }
            }
        }
    }
}