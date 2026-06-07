package com.example.foodflow.ui.components.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.StatusBadge

@Composable
fun RestaurantOrderCard(
    order: Order,
    onVerifyBankPaymentClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onReadyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${order.id.takeLast(5)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items list
            Text("Items:", fontWeight = FontWeight.SemiBold)
            val itemSummary = order.items.joinToString("\n") {
                "${it.quantity}x ${it.name}"
            }

            Text(
                text = itemSummary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Total: $${"%.2f".format(order.totalAmount)}", style = MaterialTheme.typography.titleMedium)

            if (order.transactionImageUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Payment Proof:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = order.transactionImageUrl,
                        contentDescription = "Payment Proof",
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.medium),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons based on Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (order.status) {
                    OrderStatus.PENDING_PAYMENT_VERIFICATION -> {
                        // NEW: Verify payment first
                        Button(onClick = onVerifyBankPaymentClick ) {
                            Text("Verify Payment")
                        }
                    }
                    OrderStatus.PLACED -> {
                        OutlinedButton(
                            onClick = onRejectClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Reject")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onAcceptClick) {
                            Text("Accept")
                        }
                    }
                    OrderStatus.PREPARING -> {
                        Button(onClick = onReadyClick) {
                            Text("Mark Ready for Pickup")
                        }
                    }
                    OrderStatus.READY -> {
                        Text("Waiting for driver...", color = MaterialTheme.colorScheme.primary)
                    }
                    else -> {
                        Text(order.status.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}