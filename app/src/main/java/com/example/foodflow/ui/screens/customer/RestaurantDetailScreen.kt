package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.ui.components.customer.CustomerMenuItemCard
import com.example.foodflow.ui.components.customer.PromotionBannerCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDetailViewModel

@Composable
fun RestaurantDetailScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    cartViewModel: CartViewModel,
    viewModel: RestaurantDetailViewModel = viewModel()
) {
    val restaurant by viewModel.restaurant.collectAsState()
    val promotions by viewModel.promotions.collectAsState()
    val menuItems by viewModel.filteredMenuItems.collectAsState()
    val availableCategories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState() // NEW

    RestaurantDetailContent(
        restaurant = restaurant,
        promotions = promotions,
        menuItems = menuItems,
        availableCategories = availableCategories,
        selectedCategory = selectedCategory,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onPromoClick = { promo ->
            if (promo.menuItemId.isNotEmpty()) {
                navController.navigate(Route.MenuItemDetail.createRoute(promo.menuItemId))
            }
        },
        onCategorySelected = { viewModel.selectCategory(it) },
        onAddToCartClick = { item -> cartViewModel.addItemToCart(item) },
        onMenuItemClick = { itemId ->
            navController.navigate(Route.MenuItemDetail.createRoute(itemId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailContent(
    restaurant: AppUser?,
    promotions: List<Promotion>,
    menuItems: List<MenuItem>,
    availableCategories: List<MenuItemCategory>,
    selectedCategory: String?, // NEW
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onPromoClick: (Promotion) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit,
    onMenuItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.name?.ifBlank { "Restaurant" } ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        if (restaurant == null && menuItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // --- SECTION 1: PROFILE HEADER ---
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (restaurant?.avatarUrl?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = restaurant!!.avatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(56.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(16.dp))
                            } else {
                                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(16.dp))
                            }
                            Text(
                                text = restaurant?.name?.ifBlank { "Restaurant" } ?: "Restaurant",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // --- SECTION 2: PROMOTIONS ---
                if (promotions.isNotEmpty()) {
                    item {
                        Column {
                            Text("Deals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp))
                            Spacer(Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(promotions) { promo ->
                                    PromotionBannerCard(promotion = promo, onClick = { onPromoClick(promo) })
                                }
                            }
                        }
                    }
                }

                // --- SECTION 3: CATEGORY FILTER CHIPS ---
                item {
                    // Use availableCategories directly! No more local calculation from filtered items.
                    if (availableCategories.size > 1) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedCategory == null,
                                    onClick = { onCategorySelected(null) },
                                    label = { Text("All") }
                                )
                            }
                            items(availableCategories) { category ->
                                FilterChip(
                                    selected = selectedCategory == category.id,
                                    onClick = { onCategorySelected(category.id) },
                                    label = { Text(category.name) }
                                )
                            }
                        }
                    }
                }

                // --- SECTION 4: MENU ITEMS ---
                if (menuItems.isEmpty()) {
                    item {
                        Text("No items in this category.", modifier = Modifier.padding(horizontal = 16.dp))
                    }
                } else {
                    items(menuItems) { item ->
                        CustomerMenuItemCard(
                            item = item,
                            onItemClick = { onMenuItemClick(item.id) },
                            onAddToCartClick = { onAddToCartClick(item) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}