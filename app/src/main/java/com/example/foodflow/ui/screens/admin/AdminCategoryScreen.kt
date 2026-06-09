package com.example.foodflow.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.ui.components.admin.CategoryDialog
import com.example.foodflow.ui.viewmodel.AdminViewModel

@Composable
fun AdminCategoryScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel()
) {
    val categories by adminViewModel.categories.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<MenuItemCategory?>(null) }

    if (showDialog) {
        CategoryDialog(
            category = editingCategory,
            onDismiss = { showDialog = false; editingCategory = null },
            onSave = { category, name, imageUri ->
                if (category == null) {
                    adminViewModel.addCategory(name, imageUri)
                } else {
                    // FIX: Include the 'name' parameter
                    adminViewModel.updateCategory(category, name, imageUri)
                }
                showDialog = false
                editingCategory = null
            }
        )
    }

    AdminCategoryContent(
        categories = categories,
        onBackClick = { navController.popBackStack() },
        onAddClick = { showDialog = true },
        onEditClick = { editingCategory = it; showDialog = true },
        onDeleteClick = { adminViewModel.deleteCategory(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryContent(
    categories: List<MenuItemCategory>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (MenuItemCategory) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Manage Categories") }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(
            Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) },
        floatingActionButton = { FloatingActionButton(onClick = onAddClick) { Icon(Icons.Default.Add, "Add Category") } }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (categories.isNotEmpty()) {
                items(categories, key = { it.id }) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                            AsyncImage(model = category.imageUrl, contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            Spacer(Modifier.width(16.dp))
                            Text(category.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onEditClick(category) }) { Icon(Icons.Default.Edit, "Edit") }
                            IconButton(onClick = { onDeleteClick(category.id) }) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
            else {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp) // Pushes it down slightly for a nicer UI balance
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "You have no categories! Create one.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp) // Added padding so text isn't stuck to card borders
                            )
                        }
                    }
                }
            }
        }
    }
}
