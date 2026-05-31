package com.example.foodflow.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.PlatformSettings
import com.example.foodflow.ui.viewmodel.AdminSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    viewModel: AdminSettingsViewModel = viewModel()
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Platform Settings") }) }
    ) { paddingValues ->
        when (val state = settingsState) {
            is SettingsState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SettingsState.Success -> {
                SettingsContent(
                    modifier = Modifier.padding(paddingValues),
                    initialSettings = state.settings,
                    onSave = { viewModel.saveSettings(it) }
                )
            }
            is SettingsState.Saved -> {
                // Briefly show saved, then it reloads to Success
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Settings Saved! Reloading...", style = MaterialTheme.typography.headlineSmall)
                }
            }
            is SettingsState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    initialSettings: PlatformSettings,
    onSave: (PlatformSettings) -> Unit
) {
    var deliveryFee by remember { mutableStateOf(initialSettings.deliveryFee.toString()) }
    var platformCommissionRate by remember { mutableStateOf((initialSettings.platformCommissionRate * 100).toString()) } // Display as %
    var driverCommissionRate by remember { mutableStateOf((initialSettings.driverCommissionRate * 100).toString()) } // Display as %
    var platformBankAccount by remember { mutableStateOf(initialSettings.platformBankAccount) }

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Fee Configuration", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = deliveryFee,
            onValueChange = { deliveryFee = it },
            label = { Text("Delivery Fee (\$)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = platformCommissionRate,
            onValueChange = { platformCommissionRate = it },
            label = { Text("Platform Commission Rate (%)") },
            supportingText = { Text("e.g., 10 for 10%") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = driverCommissionRate,
            onValueChange = { driverCommissionRate = it },
            label = { Text("Driver Commission Rate (%)") },
            supportingText = { Text("Percentage of the Delivery Fee the driver gets") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("Payment Configuration", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = platformBankAccount,
            onValueChange = { platformBankAccount = it },
            label = { Text("Platform Bank Account Details") },
            supportingText = { Text("Displayed to customers choosing Bank Transfer") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val newSettings = PlatformSettings(
                    deliveryFee = deliveryFee.toDoubleOrNull() ?: 0.0,
                    platformCommissionRate = (platformCommissionRate.toDoubleOrNull() ?: 0.0) / 100, // Convert % back to decimal
                    driverCommissionRate = (driverCommissionRate.toDoubleOrNull() ?: 0.0) / 100, // Convert % back to decimal
                    platformBankAccount = platformBankAccount
                )
                onSave(newSettings)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}