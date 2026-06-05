package com.example.foodflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderItem

@Composable
fun OrderShareableCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    // White background ensures it looks good when exported as an image to WhatsApp/Messenger
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = "FoodFlow Receipt",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Order #${order.id.takeLast(5)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

        // Participants
        DetailRow("Customer", order.customerName, valueColor = Color.Black, labelColor = Color.Gray)
        DetailRow("Restaurant", order.restaurantName, valueColor = Color.Black, labelColor = Color.Gray)
        DetailRow("Status", order.status.name.replace("_", " "), valueColor = Color.Black, labelColor = Color.Gray)

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

        // Items
        if (order.items.isEmpty()) {
            order.itemNames.forEach { name ->
                Text(text = "• $name", color = Color.Black, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            order.items.forEach { item ->
                OrderItemRow(item, textColor = Color.Black)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

        // Breakdown
        DetailRow("Subtotal", "$${"%.2f".format(order.subtotal)}", valueColor = Color.Black, labelColor = Color.Gray)
        DetailRow("Delivery Fee", "$${"%.2f".format(order.deliveryFee)}", valueColor = Color.Black, labelColor = Color.Gray)
        DetailRow("Platform Fee", "$${"%.2f".format(order.platformFee)}", valueColor = Color.Black, labelColor = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Text("$${"%.2f".format(order.totalAmount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.Black)
        }
    }
}

// We need to update DetailRow and OrderItemRow to accept text colors for the export card
@Composable
fun DetailRow(label: String, value: String, labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = labelColor)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

@Composable
fun OrderItemRow(item: OrderItem, textColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = textColor)
        Text(text = "$${"%.2f".format(item.price * item.quantity)}", style = MaterialTheme.typography.bodyMedium, color = textColor)
    }
}
