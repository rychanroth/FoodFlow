package com.example.foodflow.ui.components.driver

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.Order

@Composable
fun DeliveryCard(order: Order, actionText: String, onActionClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id.takeLast(5)} - ${order.status.name}", fontWeight = FontWeight.Bold)
            val itemSummary = order.items.joinToString(", ") { "${it.quantity}x ${it.name}" }
            Text(
                text = itemSummary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Text("Total: $${"%.2f".format(order.totalAmount)}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onActionClick, modifier = Modifier.fillMaxWidth()) {
                Text(actionText)
            }
        }
    }
}