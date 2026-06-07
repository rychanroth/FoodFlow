package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.navigation.Route

@Composable
fun OrderSuccessScreen(
    navController: NavController,
    orderId: String?
) {
    OrderSuccessContent(
        orderId = orderId,
        onTrackOrderClick = {
            if (orderId != null) {
                navController.navigate(Route.OrderDetail.createRoute(orderId)) {
                    popUpTo(Route.CustomerHome.route) { inclusive = false }
                }
            }
        },
        onBackToHomeClick = {
            navController.navigate(Route.CustomerHome.route) {
                popUpTo(Route.CustomerHome.route) { inclusive = true }
            }
        }
    )
}

@Composable
fun OrderSuccessContent(
    orderId: String?,
    onTrackOrderClick: () -> Unit,
    onBackToHomeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF4CAF50) // Material Green
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Order Placed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Your order has been successfully placed and is being processed.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (orderId != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Order #${orderId.takeLast(5)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onTrackOrderClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Track Order")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToHomeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}