package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.components.customer.CustomerMenuItemCard
import com.example.foodflow.ui.viewmodel.BrowseByCategoryViewModel

@Composable
fun BrowseByCategoryScreen(
    onBackClick: () -> Unit,
    onMenuItemClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit,
    viewModel: BrowseByCategoryViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Extract categoryName from SavedStateHandle (passed via navigation)
    val categoryName = viewModel.categoryName

    BrowseByCategoryContent(
        categoryName = categoryName,
        items = items,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onMenuItemClick = onMenuItemClick,
        onAddToCartClick = onAddToCartClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseByCategoryContent(
    categoryName: String,
    items: List<MenuItem>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onMenuItemClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (categoryName.isNotBlank()) categoryName else "Category") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No items found in this category.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    CustomerMenuItemCard(
                        item = item,
                        onItemClick = { onMenuItemClick(item.id) },
                        onAddToCartClick = { onAddToCartClick(item) }
                    )
                }
            }
        }
    }
}