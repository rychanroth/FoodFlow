package com.example.foodflow.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.components.AddMenuItemDialog
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantHomeScreen(
    navController: NavController,
    menuViewModel: MenuViewModel,
    authViewModel: AuthViewModel
) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Dialog state
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    // Auth navigation logic
    LaunchedEffect(authState) {
        if (authState is AuthState.Idle) {
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Show Dialog when triggered
    if (showDialog) {
        AddMenuItemDialog(
            name = itemName,
            onNameChange = { itemName = it },
            description = itemDescription,
            onDescriptionChange = { itemDescription = it },
            price = itemPrice,
            onPriceChange = { itemPrice = it },
            onDismiss = {
                showDialog = false
                // Clear fields on dismiss
                itemName = ""; itemDescription = ""; itemPrice = ""
            },
            onConfirm = {
                val priceDouble = itemPrice.toDoubleOrNull() ?: 0.0
                menuViewModel.addNewItem(itemName, itemDescription, priceDouble)
                showDialog = false
                // Clear fields on confirm
                itemName = ""; itemDescription = ""; itemPrice = ""
            }
        )
    }

    // Pass state and events down to the stateless UI
    RestaurantHomeContent(
        menuItems = menuItems,
        onLogoutClick = { authViewModel.logout() },
        onAddItemClick = { showDialog = true },
        onDeleteItemClick = { menuViewModel.deleteItem(it) }
    )
}

@ExperimentalMaterial3Api
@Composable
fun RestaurantHomeContent(
    menuItems: List<MenuItem>,
    onLogoutClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onDeleteItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Dashboard 🍳") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                            onDeleteClick = { onDeleteItemClick(item.id) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RestauarntHomeContentPreview() {
    RestaurantHomeContent(
        emptyList(),
        onLogoutClick = {},
        onAddItemClick = {},
        onDeleteItemClick = {}
    )
}