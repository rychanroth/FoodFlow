package com.example.foodflow.ui.screens.admin

import SettingsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.PlatformSettings
import com.example.foodflow.ui.components.common.FoodFlowTextField
import com.example.foodflow.ui.viewmodel.AdminSettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
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
    var platformCommissionRate by remember { mutableStateOf((initialSettings.platformCommissionRate * 100).toString()) }
    var driverCommissionRate by remember { mutableStateOf((initialSettings.driverCommissionRate * 100).toString()) }
    var platformBankAccount by remember { mutableStateOf(initialSettings.platformBankAccount) }
    var platformBankAccountUrl by remember { mutableStateOf(initialSettings.platformBankAccountUrl) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── Fee Configuration Section ──
        SectionCard(title = "Fee Configuration", icon = Icons.Outlined.AttachMoney) {
            FoodFlowTextField(
                value = deliveryFee,
                onValueChange = { deliveryFee = it },
                label = "Delivery Fee ($)",
                leadingIcon = Icons.Outlined.AttachMoney,
                keyboardType = KeyboardType.Decimal
            )
            Spacer(Modifier.height(12.dp))
            FoodFlowTextField(
                value = platformCommissionRate,
                onValueChange = { platformCommissionRate = it },
                label = "Platform Commission (%)",
                hint = "e.g., 10 for 10%",
                keyboardType = KeyboardType.Decimal
            )
            Spacer(Modifier.height(12.dp))
            FoodFlowTextField(
                value = driverCommissionRate,
                onValueChange = { driverCommissionRate = it },
                label = "Driver Commission (%)",
                hint = "Percentage of the Delivery Fee",
                keyboardType = KeyboardType.Decimal
            )
        }

        // ── Payment Configuration Section ──
        SectionCard(title = "Payment Configuration", icon = Icons.Outlined.AccountBalance) {
            FoodFlowTextField(
                value = platformBankAccount,
                onValueChange = { platformBankAccount = it },
                label = "Platform Bank Account",
                hint = "Displayed to customers for Bank Transfer",
                leadingIcon = Icons.Outlined.AccountBalance,
                keyboardType = KeyboardType.Text
            )
            Spacer(Modifier.height(12.dp))
            FoodFlowTextField(
                value = platformBankAccountUrl,
                onValueChange = { platformBankAccountUrl = it },
                label = "Bakong Dynamic URL",
                hint = "https://link.payway.com.kh/aba?id=YOUR_ID...",
                leadingIcon = Icons.Outlined.Link,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done // Last field, show "Done"
            )
        }

        // ── Save Button ──
        Button(
            onClick = {
                val newSettings = PlatformSettings(
                    deliveryFee = deliveryFee.toDoubleOrNull() ?: 0.0,
                    platformCommissionRate = (platformCommissionRate.toDoubleOrNull() ?: 0.0) / 100,
                    driverCommissionRate = (driverCommissionRate.toDoubleOrNull() ?: 0.0) / 100,
                    platformBankAccount = platformBankAccount,
                    platformBankAccountUrl = platformBankAccountUrl
                )
                onSave(newSettings)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Save Settings", modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * A clean UI wrapper to group related settings fields together.
 */
@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}