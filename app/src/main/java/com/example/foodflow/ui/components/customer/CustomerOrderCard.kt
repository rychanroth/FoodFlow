package com.example.foodflow.ui.components.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.ui.components.OrderStatusBadge
import com.example.foodflow.ui.components.OrderStatusTracker
import com.example.foodflow.ui.components.StatusBadge

@Composable
fun CustomerOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${order.id.takeLast(5)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Items: ${order.itemNames.joinToString()}", style = MaterialTheme.typography.bodyMedium)
            Text("Total: $${"%.2f".format(order.totalAmount)}", style = MaterialTheme.typography.bodyMedium)

            // Visual Tracker for Active Orders
            if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.REJECTED) {
                Spacer(modifier = Modifier.height(12.dp))
                OrderStatusTracker(order.status)
                Spacer(modifier = Modifier.height(4.dp))
                OrderStatusBadge(order.status)
            }
        }
    }
}
