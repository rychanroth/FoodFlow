package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.ui.components.customer.CategoryCard
import com.example.foodflow.ui.components.customer.CustomerMenuItemCard
import com.example.foodflow.ui.components.customer.PromotionBannerCard
import com.example.foodflow.ui.components.customer.RestaurantCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.CustomerHomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    customerViewModel: CustomerHomeViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val newlyAddedItems by customerViewModel.newlyAddedItems.collectAsState()
    val restaurants by customerViewModel.restaurants.collectAsState()
    val categories by customerViewModel.categories.collectAsState()
    val promotions by customerViewModel.promotions.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()
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
        categories = categories,
        promotions = promotions,
        newlyAddedItems = newlyAddedItems,
        restaurants = restaurants,
        isLoading = isLoading,
        cartItemCount = cartItemCount,
        onLogoutClick = { authViewModel.logout() },
        onCategoryClick = { categoryId, categoryName ->
            navController.navigate(Route.BrowseByCategory.createRoute(categoryId, categoryName))
        },
        onPromoClick = { menuItemId ->
            if (menuItemId.isNotEmpty()) {
                navController.navigate(Route.MenuItemDetail.createRoute(menuItemId))
            }
        },
        onMenuItemClick = { menuItemId ->
            navController.navigate(Route.MenuItemDetail.createRoute(menuItemId))
        },
        onAddToCartClick = { item -> cartViewModel.addItemToCart(item) },
        onRestaurantClick = { restaurantId ->
            navController.navigate(Route.RestaurantDetail.createRoute(restaurantId))
        },
        onNavigateToProfile = { navController.navigate(Route.Profile.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeContent(
    categories: List<MenuItemCategory>,
    promotions: List<Promotion>,
    newlyAddedItems: List<MenuItem>,
    restaurants: List<AppUser>,
    isLoading: Boolean,
    cartItemCount: Int,
    onLogoutClick: () -> Unit,
    onCategoryClick: (String, String) -> Unit,
    onPromoClick: (String) -> Unit,
    onMenuItemClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FoodFlow 🍔") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // --- SECTION 1: CATEGORIES ---
                if (categories.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Browse Categories")
                        Spacer(modifier = Modifier.height(8.dp))

                        // 2-Column Grid Layout
                        categories.chunked(2).forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowCategories.forEach { category ->
                                    CategoryCard(
                                        category = category,
                                        onClick = { onCategoryClick(category.id, category.name) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // If the row has only 1 item, fill the remaining space
                                if (rowCategories.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // --- SECTION 2: PROMOTIONS ---
                if (promotions.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Deals For You")
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(promotions) { promo ->
                                PromotionBannerCard(
                                    promotion = promo,
                                    onClick = { onPromoClick(promo.menuItemId) }
                                )
                            }
                        }
                    }
                }

                // --- SECTION 3: NEWLY ADDED ---
                item {
                    SectionHeader(title = "Newly Added")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (newlyAddedItems.isEmpty()) {
                    item { Text("No new items yet.", style = MaterialTheme.typography.bodyMedium) }
                } else {
                    items(newlyAddedItems) { item ->
                        CustomerMenuItemCard(
                            item = item,
                            onItemClick = { onMenuItemClick(item.id) },
                            onAddToCartClick = { onAddToCartClick(item) }
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                // --- SECTION 4: RESTAURANTS ---
                item {
                    SectionHeader(title = "Restaurants")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (restaurants.isEmpty()) {
                    item { Text("No restaurants available.", style = MaterialTheme.typography.bodyMedium) }
                } else {
                    items(restaurants) { restaurant ->
                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.uid) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// Reusable Section Header Composable
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}