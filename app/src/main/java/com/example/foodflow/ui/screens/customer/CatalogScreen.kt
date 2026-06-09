package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.ui.components.customer.CustomerMenuItemCard
import com.example.foodflow.ui.viewmodel.CatalogViewModel

@Composable
fun CatalogScreen(
    onBackClick: () -> Unit,
    onMenuItemClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

    CatalogContent(
        categories = categories,
        items = items,
        isLoading = isLoading,
        selectedCategoryId = selectedCategoryId,
        onBackClick = onBackClick,
        onCategorySelected = { viewModel.selectCategory(it) },
        onMenuItemClick = onMenuItemClick,
        onAddToCartClick = onAddToCartClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogContent(
    categories: List<MenuItemCategory>,
    items: List<MenuItem>,
    isLoading: Boolean,
    selectedCategoryId: String,
    onBackClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onMenuItemClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Catalog") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- CATEGORY FILTER CHIPS ---
            if (categories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // "All" Chip (maps to empty string in ViewModel)
                    item {
                        FilterChip(
                            selected = selectedCategoryId.isEmpty(),
                            onClick = { onCategorySelected("") },
                            label = { Text("All") }
                        )
                    }

                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.name) }
                        )
                    }
                }
                HorizontalDivider()
            }

            // --- ITEM LIST ---
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items found in this category.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
}