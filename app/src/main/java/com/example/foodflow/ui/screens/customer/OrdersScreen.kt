package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.OrderStatusBadge
import com.example.foodflow.ui.components.OrderStatusTracker
import com.example.foodflow.ui.components.StatusBadge
import com.example.foodflow.ui.components.customer.CustomerOrderCard
import com.example.foodflow.ui.viewmodel.CustomerOrdersViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrdersScreen(
    navController: NavController,
) {
    val customerOrdersViewModel: CustomerOrdersViewModel = viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val orders by customerOrdersViewModel.orders.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { customerOrdersViewModel.loadOrders(it) }
    }

    CustomerOrdersContent(orders) {
        navController.popBackStack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersContent(
    orders: List<Order>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
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
                Text("You haven't placed any orders yet.")
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
                // Separate active and past orders for better UX
                val activeOrders = orders.filter {
                    it.status != OrderStatus.DELIVERED && it.status != OrderStatus.REJECTED
                }
                val pastOrders = orders.filter {
                    it.status == OrderStatus.DELIVERED || it.status == OrderStatus.REJECTED
                }

                if (activeOrders.isNotEmpty()) {
                    item { Text("Active Orders", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                    items(activeOrders) { order ->
                        CustomerOrderCard(order = order)
                    }
                }

                if (pastOrders.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Past Orders", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    items(pastOrders) { order ->
                        CustomerOrderCard(order = order)
                    }
                }
            }
        }
    }
}