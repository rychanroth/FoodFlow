package com.example.foodflow.ui.screens.home

import android.widget.ImageButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodflow.data.model.CartItem
import com.example.foodflow.data.model.PlatformSettings
import com.example.foodflow.ui.components.OrderSummarySheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    totalPrice: Double,
    settings: PlatformSettings,
    onBackClick: () -> Unit,
    onIncreaseClick: (String) -> Unit,
    onDecreaseClick: (String) -> Unit,
    onCheckoutClick: () -> Unit
) {
    var showSummarySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // The Bottom Sheet
    if (showSummarySheet) {
        ModalBottomSheet(
            onDismissRequest = { showSummarySheet = false },
            sheetState = sheetState
        ) {
            // Pass the actual settings from CartViewModel (We'll pass it down from NavGraph next step)
            // For now, hardcode just to see the UI, then we'll wire it.
            OrderSummarySheet(
                cartItems = cartItems,
                settings = settings, // Placeholder
                onConfirmOrder = {
                    onCheckoutClick()
                    showSummarySheet = false
                },
                onDismiss = { showSummarySheet = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: $${"%.2f".format(totalPrice)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showSummarySheet = true },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Collapse Summary",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty!")
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
                items(cartItems) { cartItem ->
                    CartItemCard(
                        item = cartItem,
                        onIncreaseClick = { onIncreaseClick(cartItem.menuItem.id) },
                        onDecreaseClick = { onDecreaseClick(cartItem.menuItem.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.menuItem.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.menuItem.imageUrl,
                    contentDescription = item.menuItem.name,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.menuItem.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "$${item.menuItem.price}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecreaseClick) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }
                Text(
                    item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onIncreaseClick) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}