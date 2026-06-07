package com.example.foodflow.ui.screens.restaurant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.ui.components.common.ConfirmDialog
import com.example.foodflow.ui.components.restaurant.MenuItemCard
import com.example.foodflow.ui.components.restaurant.MenuItemDialog
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel

@Composable
fun MenuManagementScreen(
    navController: NavController,
    menuViewModel: MenuViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val categories by menuViewModel.categories.collectAsState()

    // Dialog State
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<MenuItem?>(null) }
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemCategoryId by remember { mutableStateOf("") }
    var itemIsActive by remember { mutableStateOf(true) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    LaunchedEffect(authState) {
        if (authState is AuthState.Idle) {
            menuViewModel.clearMenu()
            navController.navigate(Route.Login.route) { popUpTo(0) { inclusive = true } }
        } else if (authState is AuthState.Success) {
            val uid = menuViewModel.getCurrentUserId()
            if (uid != null) menuViewModel.loadMenuItems(uid)
        }
    }

    fun clearDialogState() {
        isDialogOpen = false; editingItem = null; itemName = ""; itemDescription = ""
        itemPrice = ""; selectedImageUri = null; itemCategoryId = ""; itemIsActive = true
    }

    if (isDialogOpen) {
        MenuItemDialog(
            isEditMode = editingItem != null,
            name = itemName, onNameChange = { itemName = it },
            description = itemDescription, onDescriptionChange = { itemDescription = it },
            price = itemPrice, onPriceChange = { itemPrice = it },
            imageModel = selectedImageUri ?: editingItem?.imageUrl,
            onPickImageClick = { galleryLauncher.launch("image/*") },
            onDismiss = { clearDialogState() },
            categories = categories, selectedCategoryId = itemCategoryId, onCategorySelected = { itemCategoryId = it },
            isActive = itemIsActive, onActiveChanged = { itemIsActive = it },
            onConfirm = {
                val priceDouble = itemPrice.toDoubleOrNull() ?: 0.0
                if (editingItem == null) {
                    menuViewModel.addNewMenuItem(itemName, itemDescription, priceDouble, itemCategoryId, itemIsActive, selectedImageUri)
                } else {
                    menuViewModel.updateMenuItem(
                        updatedItem = editingItem!!.copy(name = itemName, description = itemDescription, price = priceDouble, categoryId = itemCategoryId, isActive = itemIsActive),
                        newImageUri = selectedImageUri
                    )
                }
                clearDialogState()
            }
        )
    }

    MenuManagementContent(
        menuItems = menuItems,
        categories = categories,
        onAddItemClick = { clearDialogState(); isDialogOpen = true },
        onEditItemClick = { item ->
            editingItem = item
            itemName = item.name
            itemDescription = item.description
            itemPrice = item.price.toString()
            selectedImageUri = null
            itemCategoryId = item.categoryId
            itemIsActive = item.isActive  // This must be set BEFORE isDialogOpen = true
            isDialogOpen = true
        },
        onDeleteItemClick = { menuViewModel.deleteMenuItem(it) },
        onItemToggleActive = { item, isChecked -> menuViewModel.setMenuItemAvailability(item, isChecked) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementContent(
    menuItems: List<MenuItem>,
    categories: List<MenuItemCategory>,
    onAddItemClick: () -> Unit,
    onEditItemClick: (MenuItem) -> Unit,
    onDeleteItemClick: (String) -> Unit,
    onItemToggleActive: (MenuItem, Boolean) -> Unit
) {
    // FIX 2: Hoist the item to delete out of the LazyColumn scope
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }

    // Show dialog if itemToDelete is not null
    if (itemToDelete != null) {
        ConfirmDialog(
            showDialog = true,
            onDismiss = { itemToDelete = null },
            onConfirm = {
                onDeleteItemClick(itemToDelete!!.id)
                itemToDelete = null
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu Management") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        if (menuItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No menu items yet. Click + to add one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuItems, key = { it.id }) { item ->
                    val categoryName = categories.find { it.id == item.categoryId }?.name ?: "Uncategorized"
                    MenuItemCard(
                        item = item,
                        categoryName = categoryName,
                        onEditClick = { onEditItemClick(item) },
                        onDeleteClick = { itemToDelete = item }, // Set the item to delete
                        onToggleActive = { isChecked -> onItemToggleActive(item, isChecked) }
                    )
                }
            }
        }
    }
}
