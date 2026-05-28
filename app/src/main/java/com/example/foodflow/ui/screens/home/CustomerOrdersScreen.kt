package com.example.foodflow.ui.screens.home

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
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersScreen(
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

@Composable
fun CustomerOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${order.id.takeLast(5)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Items: ${order.itemNames.joinToString()}", style = MaterialTheme.typography.bodyMedium)
            Text("Total: $${"%.2f".format(order.totalAmount)}", style = MaterialTheme.typography.bodyMedium)

            // Visual Tracker for Active Orders
            if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.REJECTED) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = {
                        when (order.status) {
                            OrderStatus.PLACED -> 0.25f
                            OrderStatus.PREPARING -> 0.5f
                            OrderStatus.READY -> 0.75f
                            OrderStatus.ON_THE_WAY -> 0.9f
                            else -> 0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    when (order.status) {
                        OrderStatus.PLACED -> "Waiting for restaurant to accept..."
                        OrderStatus.PREPARING -> "Restaurant is cooking your food!"
                        OrderStatus.READY -> "Food is ready, waiting for driver..."
                        OrderStatus.ON_THE_WAY -> "Your driver is on the way!"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
