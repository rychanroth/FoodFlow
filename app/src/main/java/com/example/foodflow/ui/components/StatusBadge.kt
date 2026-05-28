package com.example.foodflow.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.OrderStatus

@Composable
fun StatusBadge(status: OrderStatus) {
    val color = when (status) {
        OrderStatus.PLACED -> MaterialTheme.colorScheme.error
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.tertiary
        OrderStatus.READY -> MaterialTheme.colorScheme.primary
        OrderStatus.ON_THE_WAY -> MaterialTheme.colorScheme.secondary
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.REJECTED -> MaterialTheme.colorScheme.outline
    }
    Surface(color = color.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}