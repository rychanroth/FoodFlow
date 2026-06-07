package com.example.foodflow.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.OrderStatus

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier,
    isCustomerView: Boolean = true // 🔥 Added flag to handle both use cases!
) {
    // 1. Resolve Colors
    val containerColor = when (status) {
        OrderStatus.PENDING_PAYMENT_VERIFICATION -> Color(0xFFFFF3E0)
        OrderStatus.PLACED -> MaterialTheme.colorScheme.secondaryContainer
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.tertiaryContainer
        OrderStatus.READY -> Color(0xFFE8F5E9)
        OrderStatus.ON_THE_WAY -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.DELIVERED -> Color(0xFFE3F2FD)
        OrderStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (status) {
        OrderStatus.PENDING_PAYMENT_VERIFICATION -> Color(0xFFE65100)
        OrderStatus.PLACED -> MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.READY -> Color(0xFF2E7D32)
        OrderStatus.ON_THE_WAY -> MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.DELIVERED -> Color(0xFF1565C0)
        OrderStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
    }

    // 2. Resolve Text based on target audience
    val displayText = if (isCustomerView) {
        when (status) {
            OrderStatus.PENDING_PAYMENT_VERIFICATION -> "Verifying payment..."
            OrderStatus.PLACED -> "Waiting for restaurant..."
            OrderStatus.PREPARING -> "Cooking your food!"
            OrderStatus.READY -> "Waiting for driver..."
            OrderStatus.ON_THE_WAY -> "On the way!"
            OrderStatus.DELIVERED -> "Delivered!"
            OrderStatus.REJECTED -> "Declined"
        }
    } else {
        // Fallback to cleaner version of raw enum for Restaurant/Admin view
        val locale = Locale.current.platformLocale
        status.name.replace("_", " ").lowercase(locale).replaceFirstChar { it.titlecase(locale) }
    }

    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}