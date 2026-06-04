package com.example.foodflow.ui.screens.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.viewmodel.RestaurantDashboardViewModel

/**
 * Its name reflect its feature. But not being used independently.
 * Currently being referenced by HomeScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    restaurantId: String,
    viewModel: RestaurantDashboardViewModel = viewModel()
) {
    LaunchedEffect(restaurantId) {
        viewModel.loadDashboard(restaurantId)
    }

    val todaysOrderCount by viewModel.todaysOrderCount.collectAsState()
    val todaysRevenue by viewModel.todaysRevenue.collectAsState()
    val pendingOrdersCount by viewModel.pendingOrdersCount.collectAsState()
    val todaysOrders by viewModel.todaysOrders.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Today's Overview",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Summary Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    title = "Orders",
                    value = todaysOrderCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Revenue",
                    value = "$${"%.2f".format(todaysRevenue)}",
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Pending",
                    value = pendingOrdersCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Recent Orders",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Quick list of today's orders
        if (todaysOrders.isEmpty()) {
            item {
                Text("No orders today.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(todaysOrders) { order ->
                OrderQuickCard(order = order)
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OrderQuickCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Order #${order.id.takeLast(5)}", fontWeight = FontWeight.SemiBold)
                Text(text = order.status.name, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "$${"%.2f".format(order.totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}