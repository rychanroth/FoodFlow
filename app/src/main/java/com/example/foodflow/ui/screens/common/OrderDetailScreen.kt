package com.example.foodflow.ui.screens.common

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.components.common.OrderShareableCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.screens.customer.OrderMetadataRow
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.OrderDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // NEW
    viewModel: OrderDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val userRole = currentUser?.role ?: UserRole.CUSTOMER // NEW

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order not found")
        }
    } else {
        // FIX: Extract the non-null order to a local variable
        val order = state.order!!

        OrderDetailContent(
            order = order,
            customer = state.customer,
            restaurant = state.restaurant,
            driver = state.driver,
            userRole = userRole,
            graphicsLayer = graphicsLayer,
            onBackClick = { navController.popBackStack() },
            onShareClick = {
                scope.launch(Dispatchers.IO) {
                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()

                    // FIX: Now 'order' is defined in this scope!
                    val file = File(context.cacheDir, "images/order_${order.id.takeLast(5)}.png")
                    file.parentFile?.mkdirs()
                    file.outputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/png"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Order Receipt"))
                }
            },
            onCustomerClick = { navController.navigate(Route.Profile.route) },
            onRestaurantClick = { restaurantId ->
                navController.navigate(Route.RestaurantDetail.createRoute(restaurantId))
            },
            onDriverClick = { },
            onVerifyBankPayment = { viewModel.verifyBankPayment(order.id) },
            onAcceptOrder = { viewModel.acceptOrder(order.id) },
            onRejectOrder = { viewModel.rejectOrder(order.id) },
            onMarkReady = { viewModel.markReadyForPickup(order.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    order: Order,
    customer: AppUser?,
    restaurant: AppUser?,
    driver: AppUser?,
    userRole: UserRole,
    graphicsLayer: GraphicsLayer,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onCustomerClick: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    onDriverClick: () -> Unit,
    onVerifyBankPayment: () -> Unit = {},
    onAcceptOrder: () -> Unit = {},
    onRejectOrder: () -> Unit = {},
    onMarkReady: () -> Unit = {},
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
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Share Receipt")
                    }
                }
            )
        },
        bottomBar = {
            if (userRole == UserRole.RESTAURANT || userRole == UserRole.DRIVER) {
                OrderActionBottomBar(
                    order = order,
                    userRole = userRole,
                    onVerifyBankPayment = onVerifyBankPayment,
                    onAcceptOrder = onAcceptOrder,
                    onRejectOrder = onRejectOrder,
                    onMarkReady = onMarkReady
                )
            }
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
            // Shareable Card
            OrderShareableCard(
                order = order,
                userRole = userRole, // NEW
                modifier = Modifier.drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawContent()
                }
            )

            // NEW: Metadata Section
            Text("People Involved", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            if (customer != null) {
                OrderMetadataRow(
                    label = "Customer",
                    userName = customer.name.ifBlank { customer.email },
                    avatarUrl = customer.avatarUrl,
                    onClick = onCustomerClick
                )
            }

            if (restaurant != null) {
                OrderMetadataRow(
                    label = "Restaurant",
                    userName = restaurant.name.ifBlank { restaurant.email },
                    avatarUrl = restaurant.avatarUrl,
                    onClick = { onRestaurantClick(restaurant.uid) }
                )
            }

            if (driver != null) {
                OrderMetadataRow(
                    label = "Driver",
                    userName = driver.name.ifBlank { driver.email },
                    avatarUrl = driver.avatarUrl,
                    onClick = onDriverClick
                )
            }

            if (!order.transactionImageUrl.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = order.transactionImageUrl,
                        contentDescription = "Transaction Proof",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderActionBottomBar(
    order: Order,
    userRole: UserRole,
    onVerifyBankPayment: () -> Unit,
    onAcceptOrder: () -> Unit,
    onRejectOrder: () -> Unit,
    onMarkReady: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (userRole == UserRole.RESTAURANT) {
                when (order.status) {
                    OrderStatus.PENDING_PAYMENT_VERIFICATION -> {
                        OutlinedButton(
                            onClick = onRejectOrder,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("Reject") }
                        Button(onClick = onVerifyBankPayment, modifier = Modifier.weight(1f)) { Text("Verify Payment") }
                    }
                    OrderStatus.PLACED -> {
                        OutlinedButton(
                            onClick = onRejectOrder,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("Reject") }
                        Button(onClick = onAcceptOrder, modifier = Modifier.weight(1f)) { Text("Accept Order") }
                    }
                    OrderStatus.PREPARING -> {
                        Button(onClick = onMarkReady, modifier = Modifier.fillMaxWidth()) { Text("Mark Ready for Pickup") }
                    }
                    else -> { /* No actions for READY, DELIVERED, REJECTED */ }
                }
            }

            // Placeholder for Driver Actions
            if (userRole == UserRole.DRIVER) {
                when (order.status) {
                    OrderStatus.READY -> {
                        Button(onClick = { /* TODO: Claim Order */ }, modifier = Modifier.fillMaxWidth()) { Text("Accept Delivery") }
                    }
                    OrderStatus.ON_THE_WAY -> {
                        Button(onClick = { /* TODO: Mark Delivered */ }, modifier = Modifier.fillMaxWidth()) { Text("Mark Delivered") }
                    }
                    else -> { /* No actions */ }
                }
            }
        }
    }
    }
