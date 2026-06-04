package com.example.foodflow.ui.screens.driver

import android.R.attr.order
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.DateRange
import com.example.foodflow.ui.viewmodel.DriverEarningsViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val viewModel: DriverEarningsViewModel = viewModel()
    val user by authViewModel.currentUser.collectAsState()
    val driverId = user?.uid

    val totalEarnings by viewModel.totalEarnings.collectAsState()
    val totalDeliveries by viewModel.totalDeliveries.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val selectedRange by viewModel.selectedRange.collectAsState()

    LaunchedEffect(driverId) {
        driverId?.let { viewModel.loadEarnings(it) }
    }

    if (driverId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        EarningsContent(
            totalEarnings = totalEarnings,
            totalDeliveries = totalDeliveries,
            orders = orders,
            selectedRange = selectedRange,
            onNavigateToOrderDetail = { orderId ->
                navController.navigate(Route.OrderDetail.createRoute(orderId))
          },
            onDateRangeChange = { range -> viewModel.updateDateRange(driverId, range) },
            onLogoutClick = { authViewModel.logout() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsContent(
    totalEarnings: Double,
    totalDeliveries: Int,
    orders: List<Order>,
    selectedRange: DateRange,
    onNavigateToOrderDetail: (String) -> Unit,
    onDateRangeChange: (DateRange) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Earnings") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
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
        ) {
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Total Earnings", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "$${"%.2f".format(totalEarnings)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Deliveries", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = totalDeliveries.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Date Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRange == DateRange.TODAY,
                    onClick = { onDateRangeChange(DateRange.TODAY) },
                    label = { Text("Today") }
                )
                FilterChip(
                    selected = selectedRange == DateRange.THIS_WEEK,
                    onClick = { onDateRangeChange(DateRange.THIS_WEEK) },
                    label = { Text("This Week") }
                )
                FilterChip(
                    selected = selectedRange == DateRange.THIS_MONTH,
                    onClick = { onDateRangeChange(DateRange.THIS_MONTH) },
                    label = { Text("This Month") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Earnings List
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(orders) { order ->
                    EarningItemCard(
                        order = order,
                        onClick = { onNavigateToOrderDetail(order.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun EarningItemCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Order #${order.id.takeLast(5)}", fontWeight = FontWeight.SemiBold)
                Text(text = "Delivered", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "+ $${"%.2f".format(order.driverEarnings)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Helper functions for date filters
private fun getStartOfDay(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun getStartOfWeek(): Long = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_WEEK, firstDayOfWeek); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun getStartOfMonth(): Long = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis