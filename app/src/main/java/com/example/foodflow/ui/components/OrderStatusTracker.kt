package com.example.foodflow.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.foodflow.data.model.OrderStatus

@Composable
fun OrderStatusTracker(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    // 1. Calculate and animate the progress float value dynamically
    val animatedProgress by animateFloatAsState(
        targetValue = when (status) {
            OrderStatus.PENDING_PAYMENT_VERIFICATION -> 0.1f
            OrderStatus.PLACED -> 0.25f
            OrderStatus.PREPARING -> 0.5f
            OrderStatus.READY -> 0.75f
            OrderStatus.ON_THE_WAY -> 0.9f
            OrderStatus.DELIVERED -> 1.0f // Fully filled out upon completion
            OrderStatus.REJECTED -> 0.0f
        },
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "OrderStatusProgressAnimation"
    )

    // 2. Resolve tracking bar tint accents based on failures vs standard pipelines
    val progressColor = if (status == OrderStatus.REJECTED) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    // 3. Render the progress bar UI
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier.fillMaxWidth(),
        color = progressColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}