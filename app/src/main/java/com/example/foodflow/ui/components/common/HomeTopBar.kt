package com.example.foodflow.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    userName: String?, // Pass the user's name from your AuthViewModel/Profile
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (!userName.isNullOrBlank()) "Hello, $userName 👋" else "FoodFlow 🍔",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "What would you like to eat?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            // Optional: Add a profile avatar placeholder here later!
            // IconButton(onClick = { /* TODO: Profile */ }) {
            //     Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(32.dp))
            // }
        },
        actions = {
            IconButton(onClick = onLogoutClick) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Softer than pure black
                )
            }
        }
    )
}