package com.example.foodflow.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.foodflow.data.model.CheckoutState
import com.example.foodflow.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInstructionScreen(
    orderId: String, // Need the order ID to update it
    totalAmount: Double,
    bankAccountDetails: String,
    bankPaymentUrl: String,
    cartViewModel: CartViewModel, // Pass the ViewModel directly for upload logic
    onPaymentConfirmed: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val checkoutState by cartViewModel.checkoutState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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

                    Text("Please transfer exactly:", style = MaterialTheme.typography.bodyLarge)
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

                    // Open Bank App Button
                    Button(
                        onClick = {
                            val finalUrl = bankPaymentUrl.replace("{amount}", String.format("%.2f", totalAmount))
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Bank App (ABA/Bakong)")
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    // Upload Receipt Section
                    Text("Upload Receipt", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedImageUri != null) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Receipt Preview",
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .clip(MaterialTheme.shapes.medium),
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (checkoutState is CheckoutState.Loading) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = {
                                    cartViewModel.uploadTransactionProof(
                                        orderId = orderId,
                                        imageUri = selectedImageUri!!,
                                        context = context
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Confirm Payment")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Receipt Image")
                        }
                    }
                }
            }
        }
    }

    // Listen for successful upload to navigate away
    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutState.Success && selectedImageUri != null) {
            onPaymentConfirmed()
        }
    }
}