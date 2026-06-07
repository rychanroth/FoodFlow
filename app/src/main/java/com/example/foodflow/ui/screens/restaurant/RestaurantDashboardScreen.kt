package com.example.foodflow.ui.screens.restaurant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.components.restaurant.PromotionDialog
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.SubmitPromoState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDashboardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val viewModel: RestaurantDashboardViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val user by authViewModel.currentUser.collectAsState()
    val restaurantId = user?.uid

    val todaysOrderCount by viewModel.todaysOrderCount.collectAsState()
    val todaysRevenue by viewModel.todaysRevenue.collectAsState()
    val pendingOrdersCount by viewModel.pendingOrdersCount.collectAsState()
    val todaysOrders by viewModel.todaysOrders.collectAsState()

    // Promotion State
    val promoState by viewModel.promoState.collectAsState()
    var showPromoDialog by remember { mutableStateOf(false) }
    var promoImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            promoImageUri = it
            showPromoDialog = true // Open dialog once image is picked
        }
    }

    // Handle Success state to auto-dismiss dialog
    LaunchedEffect(promoState) {
        when (promoState) {
            is SubmitPromoState.Success -> {
                showPromoDialog = false
                promoImageUri = null
                viewModel.resetPromoState()
                scope.launch {
                    snackbarHostState.showSnackbar("Promotion submitted for approval!")
                }
            }
            is SubmitPromoState.Error -> {
                // Errors are handled inside the Dialog, but we can reset state here if needed
                // or add a secondary snackbar if they dismiss the dialog before reading it.
            }
            else -> {}
        }
    }

    // Show Dialog
    if (showPromoDialog && restaurantId != null) {
        PromotionDialog(
            imageUri = promoImageUri,
            state = promoState,
            onDismiss = {
                showPromoDialog = false
                promoImageUri = null
                viewModel.resetPromoState()
            },
            onPickImage = { galleryLauncher.launch("image/*") },
            onSubmit = { viewModel.submitPromotion(restaurantId, it) }
        )
    }

    LaunchedEffect(restaurantId) {
        restaurantId?.let { viewModel.loadDashboard(it) }
    }

    if (restaurantId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        RestaurantDashboardContent(
            snackbarHostState = snackbarHostState,
            todaysOrderCount = todaysOrderCount,
            todaysRevenue = todaysRevenue,
            pendingOrdersCount = pendingOrdersCount,
            todaysOrders = todaysOrders,
            onCreatePromotionClick = { galleryLauncher.launch("image/*") }, // Trigger picker
            onNavigateToRestaurantOrders = { navController.navigate(Route.RestaurantOrders.route) },
            onLogoutClick = { authViewModel.logout() }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardContent(
    snackbarHostState: SnackbarHostState,
    todaysOrderCount: Int,
    todaysRevenue: Double,
    pendingOrdersCount: Int,
    todaysOrders: List<Order>,
    onCreatePromotionClick: () -> Unit, // NEW
    onNavigateToRestaurantOrders: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    DashboardCard(title = "Orders", value = todaysOrderCount.toString(), modifier = Modifier.weight(1f))
                    DashboardCard(title = "Revenue", value = "$${"%.2f".format(todaysRevenue)}", modifier = Modifier.weight(1f))
                    DashboardCard(title = "Pending", value = pendingOrdersCount.toString(), modifier = Modifier.weight(1f))
                }
            }

            // NEW V3: Promotional Banner CTA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Boost Your Reach", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Submit a promotional banner to be featured on the customer home screen.", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.width(8.dp))
                        FilledTonalButton(onClick = onCreatePromotionClick) {
                            Text("Create")
                        }
                    }
                }
            }

            item {
                Text(text = "Recent Orders", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }

            if (todaysOrders.isEmpty()) {
                item { Text("No orders today.", style = MaterialTheme.typography.bodyMedium) }
            } else {
                items(todaysOrders) { order -> OrderQuickCard(order = order) }
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
