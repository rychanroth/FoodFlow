package com.example.foodflow.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.OrderStatus

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    // 1. Resolve text descriptions for ALL statuses
    val statusText = when (status) {
        OrderStatus.PENDING_PAYMENT_VERIFICATION -> "Verifying payment..."
        OrderStatus.PLACED -> "Waiting for restaurant to accept..."
        OrderStatus.PREPARING -> "Restaurant is cooking your food!"
        OrderStatus.READY -> "Food is ready, waiting for driver..."
        OrderStatus.ON_THE_WAY -> "Your driver is on the way!"
        OrderStatus.DELIVERED -> "Order delivered successfully!"
        OrderStatus.REJECTED -> "Order was declined"
    }

    // 2. Resolve soft background container colors matching the state theme
    val containerColor = when (status) {
        OrderStatus.PENDING_PAYMENT_VERIFICATION -> Color(0xFFFFF3E0) // Soft Warning Amber
        OrderStatus.PLACED -> MaterialTheme.colorScheme.secondaryContainer
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.tertiaryContainer
        OrderStatus.READY -> Color(0xFFE8F5E9) // Soft Success Green
        OrderStatus.ON_THE_WAY -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.DELIVERED -> Color(0xFFE3F2FD) // Soft Informational Blue
        OrderStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
    }

    // 3. Resolve contrasting foreground typography colors
    val contentColor = when (status) {
        OrderStatus.PENDING_PAYMENT_VERIFICATION -> Color(0xFFE65100) // Deep Amber
        OrderStatus.PLACED -> MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.READY -> Color(0xFF2E7D32) // Deep Green
        OrderStatus.ON_THE_WAY -> MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.DELIVERED -> Color(0xFF1565C0) // Deep Blue
        OrderStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
    }

    // 4. Render as a cohesive, self-contained Material 3 Badge
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small // Gives it clean, slightly rounded corners
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}