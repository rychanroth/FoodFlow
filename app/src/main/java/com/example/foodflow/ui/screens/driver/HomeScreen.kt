package com.example.foodflow.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.DriverOrdersViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    driverViewModel: DriverOrdersViewModel = viewModel()
) {
    val currentDriverId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    LaunchedEffect(Unit) {
        driverViewModel.loadAvailableOrders()
        driverViewModel.loadMyActiveDeliveries(currentDriverId)
    }

    val availableOrders by driverViewModel.availableOrders.collectAsState()
    val myActiveDeliveries by driverViewModel.myActiveDeliveries.collectAsState()

    DriverHomeContent(
        availableOrders = availableOrders,
        myActiveDeliveries = myActiveDeliveries,
        onAcceptClick = { orderId -> driverViewModel.acceptOrder(orderId, currentDriverId) },
        onDeliveredClick = { orderId -> driverViewModel.markAsDelivered(orderId) },
        onLogoutClick = { authViewModel.logout() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverHomeContent(
    availableOrders: List<Order>,
    myActiveDeliveries: List<Order>,
    onAcceptClick: (String) -> Unit,
    onDeliveredClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard 🛵") },
                actions = {
                    TextButton(onClick = onLogoutClick) { Text("Logout") }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Deliveries
            if (myActiveDeliveries.isNotEmpty()) {
                item { Text("My Active Delivery", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(myActiveDeliveries) { order ->
                    DeliveryCard(
                        order = order,
                        actionText = "Mark Delivered",
                        onActionClick = { onDeliveredClick(order.id) }
                    )
                }
            }

            // Available Orders
            item { Text("Available Pickups", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            if (availableOrders.isEmpty()) {
                item { Text("No orders ready for pickup right now.") }
            } else {
                items(availableOrders) { order ->
                    DeliveryCard(
                        order = order,
                        actionText = "Accept Pickup",
                        onActionClick = { onAcceptClick(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryCard(order: Order, actionText: String, onActionClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id.takeLast(5)} - ${order.status.name}", fontWeight = FontWeight.Bold)
            Text("Items: ${order.itemNames.joinToString()}")
            Text("Total: $${"%.2f".format(order.totalAmount)}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onActionClick, modifier = Modifier.fillMaxWidth()) {
                Text(actionText)
            }
        }
    }
}