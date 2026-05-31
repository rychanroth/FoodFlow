package com.example.foodflow.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.viewmodel.AuthViewModel

@Composable
fun AdminDashboardScreen(
    authViewModel: AuthViewModel,
) {
    AdminDashboardContent(
        onLogoutClick = { authViewModel.logout() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FoodFlow Admin") },
                actions = {
                    TextButton(onClick = onLogoutClick) { Text("Logout") }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Platform Dashboard", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analytics and platform settings will appear here.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}