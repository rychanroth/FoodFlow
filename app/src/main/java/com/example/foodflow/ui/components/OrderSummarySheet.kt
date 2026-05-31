package com.example.foodflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.CartItem
import com.example.foodflow.data.model.PlatformSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummarySheet(
    cartItems: List<CartItem>,
    settings: PlatformSettings,
    onConfirmOrder: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Order Summary", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Item List
        cartItems.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${item.quantity}x ${item.menuItem.name}", modifier = Modifier.weight(1f))
                Text("$${String.format("%.2f", item.menuItem.price * item.quantity)}")
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Fee Breakdown
        val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal")
            Text("$${String.format("%.2f", subtotal)}")
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery Fee")
            Text("$${String.format("%.2f", settings.deliveryFee)}")
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Platform Fee")
            Text("$${String.format("%.2f", settings.platformFlatFee)}")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Total
        val total = subtotal + settings.deliveryFee + settings.platformFlatFee
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("$${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Confirm Button
        Button(
            onClick = onConfirmOrder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order (COD)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderSummarySheetPreview() {
    OrderSummarySheet(
        cartItems = TODO(),
        settings = TODO(),
        onConfirmOrder = TODO(),
        onDismiss = TODO()
    )
}