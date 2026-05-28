package com.example.foodflow.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.CustomerHomeViewModel

@Composable
fun CustomerHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    customerViewModel: CustomerHomeViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val newlyAddedItems by customerViewModel.newlyAddedItems.collectAsState()
    val restaurants by customerViewModel.restaurants.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }

    LaunchedEffect(authState) {
        if (authState is AuthState.Idle) {
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    CustomerHomeContent(
        newlyAddedItems = newlyAddedItems,
        restaurants = restaurants,
        cartItemCount = cartItemCount,
        onLogoutClick = { authViewModel.logout() },
        onRestaurantClick = { restaurantId ->
            navController.navigate(Route.RestaurantDetail.createRoute(restaurantId))
        },
        onCartClick = {
            navController.navigate(Route.Cart.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeContent(
    newlyAddedItems: List<MenuItem>,
    restaurants: List<AppUser>,
    cartItemCount: Int,
    onLogoutClick: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    onCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FoodFlow 🍔") },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Newly Added",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (newlyAddedItems.isEmpty()) {
                    Text("No new items yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(newlyAddedItems) { item ->
                            FoodCard(item = item)
                        }
                    }
                }
            }

            item {
                Text(
                    "Restaurants",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (restaurants.isEmpty()) {
                item {
                    Text(
                        "No restaurants available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(restaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant.uid) }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodCard(item: MenuItem) {
    Card(
        modifier = Modifier.width(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text("$${item.price}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RestaurantCard(restaurant: AppUser, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = "Restaurant",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(restaurant.email, style = MaterialTheme.typography.titleMedium)
                Text("Tap to view menu", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}