package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.ui.components.common.HomeTopBar
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
fun CustomerHomeScreen(
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
    val user by authViewModel.currentUser.collectAsState()

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
        user = user,
        categories = categories,
        promotions = promotions,
        newlyAddedItems = newlyAddedItems,
        restaurants = restaurants,
        isLoading = isLoading,
        onLogoutClick = { authViewModel.logout() },
        onCategoryClick = { categoryId, categoryName ->
            navController.navigate(Route.Catalog.createRoute(categoryId, categoryName))
        },
        onSeeAllCategoriesClick = {
            navController.navigate(Route.Catalog.createRoute()) // No ID passed = See All
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
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CustomerHomeContent(
    user: AppUser?,
    categories: List<MenuItemCategory>,
    promotions: List<Promotion>,
    newlyAddedItems: List<MenuItem>,
    restaurants: List<AppUser>,
    isLoading: Boolean,
    onLogoutClick: () -> Unit,
    onCategoryClick: (String, String) -> Unit,
    onSeeAllCategoriesClick: () -> Unit,
    onPromoClick: (String) -> Unit,
    onMenuItemClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit,
    onRestaurantClick: (String) -> Unit,
) {
    val columns = rememberGridColumnCount()

    Scaffold(
        topBar = {
            HomeTopBar(
                userName = user?.name,
                onLogoutClick = onLogoutClick
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- SECTION 1: CATEGORIES ---
                if (categories.isNotEmpty()) {
                    // Header spans full width
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionHeader(title = "Browse Categories")
                            TextButton(onClick = onSeeAllCategoriesClick) {
                                Text("See All", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Categories wrapper spans full width
                    val itemsToShow = if (columns == 6) categories.take(6) else categories.take(4)

                    items(itemsToShow, key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onCategoryClick(category.id, category.name) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // --- SECTION 2: PROMOTIONS ---
                if (promotions.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
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
                }

                // --- SECTION 3: NEWLY ADDED ---
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(title = "Newly Added")
                }

                if (newlyAddedItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("No new items yet.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    // ✅ GRID ITEMS: Automatically falls into the adaptive columns
                    items(newlyAddedItems, key = { it.id }, span = { GridItemSpan(maxLineSpan) }) { item ->
                        CustomerMenuItemCard(
                            item = item,
                            onItemClick = { onMenuItemClick(item.id) },
                            onAddToCartClick = { onAddToCartClick(item) }
                        )
                    }
                }

                // --- SECTION 4: RESTAURANTS ---
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(title = "Restaurants")
                }

                if (restaurants.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("No restaurants available.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    // ✅ GRID ITEMS: Automatically falls into the adaptive columns
                    items(restaurants, key = { it.uid }) { restaurant ->
                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerHomeContentPreview() {
    // 1. Mock Data Setup (Adjust properties to match your exact data class definitions)
    val mockUser = AppUser(
        uid = "user_01",
        name = "Alex Johnson",
        isProfileComplete = true
    )

    val mockCategories = listOf(
        MenuItemCategory(id = "cat_1", name = "Burgers", imageUrl = "https://example.com/burger.jpg"),
        MenuItemCategory(id = "cat_2", name = "Pizza", imageUrl = ""), // Tests your ImageVector fallback fix!
        MenuItemCategory(id = "cat_3", name = "Sushi", imageUrl = "https://example.com/sushi.jpg"),
        MenuItemCategory(id = "cat_4", name = "Desserts", imageUrl = "https://example.com/dessert.jpg"),
        MenuItemCategory(id = "cat_5", name = "Drinks", imageUrl = "https://example.com/drinks.jpg")
    )

    val mockPromotions = listOf(
        Promotion(id = "promo_1", menuItemId = "item_1"),
        Promotion(id = "promo_2", menuItemId = "item_3")
    )

    val mockMenuItems = listOf(
        MenuItem(id = "item_1", name = "Classic Smash Burger", price = 9.99, description = "Double patty with cheese"),
        MenuItem(id = "item_2", name = "Truffle Parmesan Fries", price = 4.99, description = "Crispy golden fries with real truffle oil"),
        MenuItem(id = "item_3", name = "Spicy Pepperoni Pizza", price = 14.99, description = "Hot honey drizzle and fresh basil")
    )

    val mockRestaurants = listOf(
        AppUser(uid = "rest_1", name = "The Burger Joint", isProfileComplete = true),
        AppUser(uid = "rest_2", name = "Pizza Supreme", isProfileComplete = true),
        AppUser(uid = "rest_3", name = "Tokyo Sushi Express", isProfileComplete = true)
    )

    // 2. Render Component Inside Project Theme
    MaterialTheme {
        CustomerHomeContent(
            user = mockUser,
            categories = mockCategories,
            promotions = mockPromotions,
            newlyAddedItems = mockMenuItems,
            restaurants = mockRestaurants,
            isLoading = false,
            onLogoutClick = {},
            onCategoryClick = { _, _ -> },
            onSeeAllCategoriesClick = {},
            onPromoClick = {},
            onMenuItemClick = {},
            onAddToCartClick = {},
            onRestaurantClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun CustomerHomeContentLoadingPreview() {
    MaterialTheme {
        CustomerHomeContent(
            user = null,
            categories = emptyList(),
            promotions = emptyList(),
            newlyAddedItems = emptyList(),
            restaurants = emptyList(),
            isLoading = true,
            onLogoutClick = {},
            onCategoryClick = { _, _ -> },
            onSeeAllCategoriesClick = {},
            onPromoClick = {},
            onMenuItemClick = {},
            onAddToCartClick = {},
            onRestaurantClick = {}
        )
    }
}

// Reusable Section Header Composable
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}


// Use local config to adapt to different screen
@Composable
fun rememberGridColumnCount(): Int {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp >= 840 -> 6
        else -> 2
    }
}