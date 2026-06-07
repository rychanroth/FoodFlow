package com.example.foodflow.ui.screens.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.StatusBadge
import com.example.foodflow.ui.components.restaurant.RestaurantOrderCard
import com.example.foodflow.ui.viewmodel.RestaurantOrdersViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: RestaurantOrdersViewModel = viewModel()
) {
    // Fetch the current restaurant's ID to load their specific orders
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Trigger the load ONLY ONCE when the screen is first composed
    LaunchedEffect(currentUserId) {
        viewModel.loadOrders(currentUserId)
    }

    val orders by viewModel.orders.collectAsState()

    RestaurantOrdersContent(
        orders = orders,
        onBackClick = { navController.popBackStack() },
        onVerifyBankPaymentClick = { viewModel.verifyBankPayment(it) },
        onAcceptClick = { viewModel.acceptOrder(it) },
        onRejectClick = { viewModel.rejectOrder(it) },
        onReadyClick = { viewModel.markReadyForPickup(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantOrdersContent(
    orders: List<Order>,
    onBackClick: () -> Unit,
    onVerifyBankPaymentClick: (String) -> Unit,
    onAcceptClick: (String) -> Unit,
    onRejectClick: (String) -> Unit,
    onReadyClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incoming Orders") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No incoming orders yet.")
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
                items(orders, key = { it.id }) { order ->
                    RestaurantOrderCard(
                        order = order,
                        onVerifyBankPaymentClick = { onVerifyBankPaymentClick(order.id)},
                        onAcceptClick = { onAcceptClick(order.id) },
                        onRejectClick = { onRejectClick(order.id) },
                        onReadyClick = { onReadyClick(order.id) }
                    )
                }
            }
        }
    }
}

