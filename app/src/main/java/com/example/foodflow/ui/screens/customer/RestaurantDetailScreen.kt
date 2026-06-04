package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.components.customer.CustomerMenuItemCard
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDetailViewModel

@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    restaurantName: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: RestaurantDetailViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    LaunchedEffect(restaurantId) {
        viewModel.loadMenu(restaurantId)
    }

    val menuItems by viewModel.menuItems.collectAsState()

    RestaurantDetailContent(
        restaurantName = restaurantName,
        menuItems = menuItems,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onAddToCartClick = { item ->
            cartViewModel.addItemToCart(item)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailContent(
    restaurantName: String,
    menuItems: List<MenuItem>,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onAddToCartClick: (MenuItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurantName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (menuItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(menuItems) { item ->
                    CustomerMenuItemCard(
                        item = item,
                        onAddToCartClick = { onAddToCartClick(item) }
                    )
                }
            }
        }
    }
}

