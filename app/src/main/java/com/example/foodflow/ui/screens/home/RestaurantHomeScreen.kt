package com.example.foodflow.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantHomeScreen(
    menuViewModel: MenuViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    // Collect the real-time menu state from the ViewModel
    val menuItems by menuViewModel.menuItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Dashboard") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { menuViewModel.addDummyItem() }
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Restaurant Dashboard 🍳",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (menuItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No menu items yet. Click + to add one!")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(menuItems, key = { it.id }) { item ->
                        MenuItemCard(
                            item = item,
                            onDeleteClick = { menuViewModel.deleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleLarge)
                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                Text("$${item.price}", style = MaterialTheme.typography.bodyLarge)
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}