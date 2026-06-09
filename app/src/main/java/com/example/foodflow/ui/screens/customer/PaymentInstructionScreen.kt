package com.example.foodflow.ui.screens.customer

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.CheckoutState
import com.example.foodflow.ui.viewmodel.CartViewModel

@Composable
fun PaymentInstructionScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current

    val settings by cartViewModel.settings.collectAsState()
    val checkoutState by cartViewModel.checkoutState.collectAsState()

    val orderId = cartViewModel.lastOrderId
    val totalAmount = cartViewModel.lastOrderTotal

    if (orderId == null) {
        LaunchedEffect(Unit) { navController.navigate(Route.CustomerHome.route) }
        return
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    PaymentInstructionContent(
        totalAmount = totalAmount,
        bankAccountDetails = settings.platformBankAccount,
        checkoutState = checkoutState,
        selectedImageUri = selectedImageUri,
        onSelectImageClick = { galleryLauncher.launch("image/*") },
        onOpenBankAppClick = {
            val finalUrl = settings.platformBankAccountUrl.replace("{amount}", String.format("%.2f", totalAmount))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
            context.startActivity(intent)
        },
        onConfirmPaymentClick = {
            selectedImageUri?.let { uri ->
                cartViewModel.uploadTransactionProof(
                    orderId = orderId,
                    imageUri = uri,
                    context = context
                )
            }
        },
        onBackClick = { navController.navigate(Route.CustomerHome.route) }
    )

    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutState.Success && selectedImageUri != null) {
            val currentOrderId = cartViewModel.lastOrderId ?: return@LaunchedEffect
            navController.navigate(Route.OrderSuccess.createRoute(currentOrderId)) {
                popUpTo(Route.CustomerHome.route) { inclusive = false }
            }
            cartViewModel.resetCheckoutState()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInstructionContent(
    totalAmount: Double,
    bankAccountDetails: String,
    checkoutState: CheckoutState,
    selectedImageUri: Uri?,
    onSelectImageClick: () -> Unit,
    onOpenBankAppClick: () -> Unit,
    onConfirmPaymentClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Payment Required", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Please transfer exactly:", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "$${"%.2f".format(totalAmount)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("To Account:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = bankAccountDetails,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onOpenBankAppClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Bank App (ABA/Bakong)")
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Upload Receipt", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Receipt Preview",
                            modifier = Modifier
                                .heightIn(max = 250.dp) // Safely limits size inside a vertical scrollable view
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium),
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (checkoutState is CheckoutState.Loading) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = onConfirmPaymentClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Confirm Payment")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSelectImageClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Receipt Image")
                        }
                    }
                }
            }
        }
    }
}