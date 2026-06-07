package com.example.foodflow.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderItem
import com.example.foodflow.data.model.UserRole

@Composable
fun OrderShareableCard(
    order: Order,
    userRole: UserRole, // NEW
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order Receipt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OrderStatusBadge(order.status)
            }
            Spacer(Modifier.height(8.dp))
            Text("Order #${order.id.takeLast(5)}", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            // Items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!item.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = "${item.quantity}x ${item.name}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "$${String.format("%.2f", item.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            // Financials - Role Based Visibility
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal"); Text("$${String.format("%.2f", order.subtotal)}")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery Fee"); Text("$${String.format("%.2f", order.deliveryFee)}")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Platform Fee"); Text("$${String.format("%.2f", order.platformFee)}")
            }

            // RESTAURANT & ADMIN ONLY
            if (userRole == UserRole.RESTAURANT || userRole == UserRole.ADMIN) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Restaurant Earnings", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("$${String.format("%.2f", order.restaurantEarnings)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            // DRIVER & ADMIN ONLY
            if (userRole == UserRole.DRIVER || userRole == UserRole.ADMIN) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Driver Earnings", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("$${String.format("%.2f", order.driverEarnings)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            // ADMIN ONLY
            if (userRole == UserRole.ADMIN) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Platform Earnings", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Text("$${String.format("%.2f", order.platformEarnings)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 4.dp))

            // Total Paid (Visible to all)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("$${String.format("%.2f", order.totalAmount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Optional: Thumbnail
        if (item.imageUrl.isNotBlank()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = "${item.quantity}x ${item.name}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = textColor
        )
        Text(
            text = "$${"%.2f".format(item.price * item.quantity)}",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}