package com.example.foodflow.ui.screens.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.components.common.HomeTopBar
import com.example.foodflow.ui.components.driver.DeliveryCard
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
    val user by authViewModel.currentUser.collectAsState()

    DriverHomeContent(
        user = user,
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
    user: AppUser?,
    availableOrders: List<Order>,
    myActiveDeliveries: List<Order>,
    onAcceptClick: (String) -> Unit,
    onDeliveredClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                userName = user?.name,
                onLogoutClick = onLogoutClick
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