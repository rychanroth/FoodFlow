package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.components.customer.CustomerMenuItemCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDetailViewModel

@Composable
fun RestaurantDetailScreen(
    navController: NavController,
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
        },
        onNavigateToMenuItemDetail = { itemId ->
            navController.navigate(Route.MenuItemDetail.createRoute(itemId))
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
    onAddToCartClick: (MenuItem) -> Unit,
    onNavigateToMenuItemDetail: (String) -> Unit
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
                        onAddToCartClick = { onAddToCartClick(item) },
                        onItemClick = { onNavigateToMenuItemDetail(item.id) }
                    )
                }
            }
        }
    }
}

