package com.example.foodflow.ui.screens.restaurant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.components.MenuItemDialog
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    menuViewModel: MenuViewModel,
    authViewModel: AuthViewModel
) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Dialog State
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<MenuItem?>(null) }
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    // NEW: Image URI State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // NEW: The Activity Result Launcher for picking images
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // When the user picks an image, update our hoisted state
        selectedImageUri = uri
    }

    // NEW: Load menu items ONLY when we confirm the user is logged in
    LaunchedEffect(authState) {
        if (authState is AuthState.Idle) {
            menuViewModel.clearMenu() // Clear data on logout
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        } else if (authState is AuthState.Success) {
            // The user is guaranteed to exist here
            val uid = menuViewModel.getCurrentUserId()
            if (uid != null) {
                menuViewModel.loadMenuItems(uid)
            }
        }
    }

    fun clearDialogState() {
        isDialogOpen = false
        editingItem = null
        itemName = ""
        itemDescription = ""
        itemPrice = ""
        selectedImageUri = null // Clear image too
    }

    if (isDialogOpen) {
        MenuItemDialog(
            isEditMode = editingItem != null,
            name = itemName,
            onNameChange = { itemName = it },
            description = itemDescription,
            onDescriptionChange = { itemDescription = it },
            price = itemPrice,
            onPriceChange = { itemPrice = it },
            // FIX: Show the newly picked local URI, OR fall back to the existing remote URL string
            imageModel = selectedImageUri ?: editingItem?.imageUrl,
            onPickImageClick = { galleryLauncher.launch("image/*") },
            onDismiss = { clearDialogState() },
            onConfirm = {
                val priceDouble = itemPrice.toDoubleOrNull() ?: 0.0
                if (editingItem == null) {
                    menuViewModel.addNewMenuItem(itemName, itemDescription, priceDouble, selectedImageUri)
                } else {
                    menuViewModel.updateMenuItem(
                        updatedItem = editingItem!!.copy(name = itemName, description = itemDescription, price = priceDouble),
                        newImageUri = selectedImageUri // Passes null if they didn't pick a new one, keeping the old URL!
                    )
                }
                clearDialogState()
            }
        )
    }

    RestaurantHomeContent(
        menuItems = menuItems,
        onLogoutClick = { authViewModel.logout() },
        onAddItemClick = {
            clearDialogState()
            isDialogOpen = true
        },
        onEditItemClick = { item ->
            editingItem = item
            itemName = item.name
            itemDescription = item.description
            itemPrice = item.price.toString()
            // FIX: DO NOT parse the URL. Leave selectedImageUri null.
            // The Dialog will use editingItem.imageUrl to display the current image.
            selectedImageUri = null
            isDialogOpen = true
        },
        onDeleteItemClick = { menuViewModel.deleteMenuItem(it) },
        onNavigateToRestaurantOrders = { navController.navigate(Route.RestaurantOrders.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantHomeContent(
    menuItems: List<MenuItem>,
    onLogoutClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onEditItemClick: (MenuItem) -> Unit,
    onDeleteItemClick: (String) -> Unit,
    onNavigateToRestaurantOrders: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToRestaurantOrders) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Orders")
                    }
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
                            onEditClick = { onEditItemClick(item) },
                            onDeleteClick = { onDeleteItemClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
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
            // Show Image from URL if it exists
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleLarge)
                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                Text("$${item.price}", style = MaterialTheme.typography.bodyLarge)
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RestauarntHomeContentPreview() {
    RestaurantHomeContent(
        menuItems = listOf(
            MenuItem(id = "1", name = "Pizza", description = "Delicious cheese pizza", price = 12.99),
            MenuItem(id = "2", name = "Burger", description = "Juicy beef burger", price = 8.99)
        ),
        onLogoutClick = {},
        onAddItemClick = {},
        onEditItemClick = {},
        onDeleteItemClick = {},
        onNavigateToRestaurantOrders = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MenuItemCardPreview() {
    MenuItemCard(
        item = MenuItem(
            id = "1",
            name = "Sample Item",
            description = "This is a sample description",
            price = 9.99
        ),
        onEditClick = {},
        onDeleteClick = {}
    ) 
}