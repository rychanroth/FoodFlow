package com.example.foodflow.ui.screens.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDashboardViewModel

/**
 * Its name reflect its feature. But not being used independently.
 * Currently being referenced by HomeScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val viewModel: RestaurantDashboardViewModel = viewModel()

    val user by authViewModel.currentUser.collectAsState()
    val restaurantId = user?.uid

    val todaysOrderCount by viewModel.todaysOrderCount.collectAsState()
    val todaysRevenue by viewModel.todaysRevenue.collectAsState()
    val pendingOrdersCount by viewModel.pendingOrdersCount.collectAsState()
    val todaysOrders by viewModel.todaysOrders.collectAsState()

    LaunchedEffect(restaurantId) {
        restaurantId?.let { id ->
            viewModel.loadDashboard(id)
        }
    }

    if (restaurantId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        RestaurantDashboardContent(
            todaysOrderCount = todaysOrderCount,
            todaysRevenue = todaysRevenue,
            pendingOrdersCount = pendingOrdersCount,
            todaysOrders = todaysOrders,
            onNavigateToRestaurantOrders = { navController.navigate(Route.RestaurantOrders.route) },
            onLogoutClick = { authViewModel.logout() }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardContent(
    todaysOrderCount: Int,
    todaysRevenue: Double,
    pendingOrdersCount: Int,
    todaysOrders: List<Order>,
    onNavigateToRestaurantOrders: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToRestaurantOrders) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Orders")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

@Preview(showBackground = true)
@Composable
fun RestaurantDashboardContentPreview() {
    RestaurantDashboardContent(
        todaysOrderCount = TODO(),
        todaysRevenue = TODO(),
        pendingOrdersCount = TODO(),
        todaysOrders = TODO(),
        onNavigateToRestaurantOrders = TODO(),
        onLogoutClick = TODO(),
        modifier = TODO()
    )
}