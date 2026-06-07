package com.example.foodflow.ui.components.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderItem
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.common.OrderStatusTracker
import com.example.foodflow.ui.components.common.OrderStatusBadge

@Composable
fun CustomerOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Make the whole card clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Restaurant Name + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.restaurantName, // Using our new denormalized field!
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item Summary
            val itemSummary = order.items.take(3).joinToString(", ") {
                "${it.quantity}x ${it.name}"
            } + if (order.items.size > 3) "..." else ""

            Text(
                text = itemSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Total Amount & Tracker
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "$${"%.2f".format(order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Only show the step tracker for active orders to save space
                if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.REJECTED) {
                    OrderStatusTracker(order.status)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerOrderCardPreview() {
    CustomerOrderCard(
        order = Order(
            restaurantName = "Pizza Palace",
            status = OrderStatus.ON_THE_WAY ,
            items = listOf(
                OrderItem(name = "Margherita Pizza", quantity = 1),
                OrderItem(name = "Garlic Knots", quantity = 2)
            ),
            totalAmount = 24.50
        ),
        onClick = {}
    )
}