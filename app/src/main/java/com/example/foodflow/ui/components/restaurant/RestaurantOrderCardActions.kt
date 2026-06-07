package com.example.foodflow.ui.components.restaurant

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus

@Composable
fun RestaurantOrderCardActions(
    order: Order,
    onVerifyBankPaymentClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onReadyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        when (order.status) {
            OrderStatus.PENDING_PAYMENT_VERIFICATION -> {
                Button(
                    onClick = onVerifyBankPaymentClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Verify Payment")
                }
            }

            OrderStatus.PLACED -> {
                OutlinedButton(
                    onClick = onRejectClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reject")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onAcceptClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept")
                }
            }

            OrderStatus.PREPARING -> {
                Button(
                    onClick = onReadyClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Mark Ready for Pickup")
                }
            }

            OrderStatus.READY -> {
                Text(
                    text = "Waiting for driver...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            else -> {
                val currentPlatformLocale = Locale.current.platformLocale
                val fallbackReadableText = order.status.name
                    .replace("_", " ")
                    .lowercase(currentPlatformLocale)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(currentPlatformLocale) else it.toString() }

                Text(
                    text = fallbackReadableText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}