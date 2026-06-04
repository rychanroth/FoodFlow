package com.example.foodflow.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderItem
import com.example.foodflow.ui.viewmodel.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val order by viewModel.order.collectAsState()

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        OrderDetailContent(
            order = order!!,
            onBackClick = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    order: Order,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #${order.id.takeLast(5)}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status & Date Header
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = order.status.name.replace("_", " "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            com.example.foodflow.data.model.OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                            com.example.foodflow.data.model.OrderStatus.REJECTED -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Placed on: ${java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a", java.util.Locale.getDefault()).format(order.createdAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Participants Info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Customer", order.customerName)
                    DetailRow("Delivery Address", order.deliveryAddress.ifBlank { "N/A" })
                    DetailRow("Restaurant", order.restaurantName)
                    DetailRow("Payment Method", order.paymentMethod.name)

                    if (order.transactionImageUrl != null) {
                        Text("Bank Transfer Proof Uploaded", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Order Items
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (order.items.isEmpty()) {
                        // Fallback for legacy V1 orders that only have itemNames
                        order.itemNames.forEach { name ->
                            Text(text = "• $name", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        order.items.forEach { item ->
                            OrderItemRow(item)
                        }
                    }
                }
            }

            // Receipt Breakdown
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Payment Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("Subtotal", "$${"%.2f".format(order.subtotal)}")
                    DetailRow("Delivery Fee", "$${"%.2f".format(order.deliveryFee)}")
                    DetailRow("Platform Fee", "$${"%.2f".format(order.platformFee)}")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$${"%.2f".format(order.totalAmount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            // Earnings Breakdown (Crucial for Driver/Restaurant/Admin)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Earnings Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("Restaurant Earns", "$${"%.2f".format(order.restaurantEarnings)}")
                    DetailRow("Driver Earns", "$${"%.2f".format(order.driverEarnings)}")
                    DetailRow("Platform Earns", "$${"%.2f".format(order.platformEarnings)}")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(text = "$${"%.2f".format(item.price * item.quantity)}", style = MaterialTheme.typography.bodyMedium)
    }
}