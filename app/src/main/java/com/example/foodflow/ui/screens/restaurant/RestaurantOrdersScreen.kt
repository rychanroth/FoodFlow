package com.example.foodflow.ui.screens.restaurant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.common.OrderStatusBadge
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.RestaurantOrdersViewModel

@Composable
fun RestaurantOrdersScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: RestaurantOrdersViewModel = viewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    LaunchedEffect(user) {
        user?.uid?.let { viewModel.loadOrders(it) }
    }

    RestaurantOrdersContent(
        orders = filteredOrders,
        selectedStatus = selectedStatus,
        onStatusSelected = { viewModel.selectStatus(it) },
        onOrderClick = { orderId ->
            navController.navigate(Route.OrderDetail.createRoute(orderId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantOrdersContent(
    orders: List<Order>,
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit,
    onOrderClick: (String) -> Unit
) {
    // Define the statuses relevant to a Restaurant
    val filterStatuses = listOf(
        OrderStatus.PENDING_PAYMENT_VERIFICATION,
        OrderStatus.PLACED,
        OrderStatus.PREPARING,
        OrderStatus.READY
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Orders") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- FILTER CHIPS ---
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onStatusSelected(null) },
                        label = { Text("All") }
                    )
                }
                items(filterStatuses) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusSelected(status) },
                        label = { Text(status.name.replace("_", " ")) } // Format nicely
                    )
                }
            }

            HorizontalDivider()

            // --- ORDER LIST ---
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No orders found for this filter.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        RestaurantOrderPreviewCard(
                            order = order,
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantOrderPreviewCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order #${order.id.takeLast(5)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.customerName, // Using denormalized data
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                OrderStatusBadge(status = order.status) // Re-use existing badge if you have it
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}