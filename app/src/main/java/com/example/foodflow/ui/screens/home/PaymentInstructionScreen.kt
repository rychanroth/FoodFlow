package com.example.foodflow.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInstructionScreen(
    totalAmount: Double,
    bankAccountDetails: String,
    bankPaymentUrl: String, // The ABA URL template
    onPaymentConfirmed: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Payment") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Payment Required", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Please transfer exactly:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "$${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("To Account:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        bankAccountDetails,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // The Magic Button: Opens ABA/Bakong App
                    Button(
                        onClick = {
                            // Replace {amount} in the URL template
                            val finalUrl = bankPaymentUrl.replace("{amount}", totalAmount.toString())
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Bank App (ABA/Bakong)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "After completing the transfer, click the button below.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onPaymentConfirmed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I have paid")
                    }
                }
            }
        }
    }
}