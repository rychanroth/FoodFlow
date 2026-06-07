package com.example.foodflow.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.foodflow.ui.screens.common.MenuItemDetailScreen
import com.example.foodflow.ui.screens.common.OrderDetailScreen
import com.example.foodflow.ui.screens.common.ProfileScreen
import com.example.foodflow.ui.screens.customer.CartScreen
import com.example.foodflow.ui.screens.customer.CatalogScreen
import com.example.foodflow.ui.screens.customer.FavoritesScreen
import com.example.foodflow.ui.screens.customer.HomeScreen
import com.example.foodflow.ui.screens.customer.OrdersScreen
import com.example.foodflow.ui.screens.customer.PaymentInstructionScreen
import com.example.foodflow.ui.screens.customer.RestaurantDetailScreen
import com.example.foodflow.ui.screens.customer.SearchScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.CustomerHomeViewModel
import com.example.foodflow.ui.viewmodel.SettingsViewModel

fun NavGraphBuilder.customerGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    settingsViewModel: SettingsViewModel
) {
    // FIX: Wrap in navigation block
    navigation(
        startDestination = Route.CustomerHome.route,
        route = Route.CustomerGraph.route
    ) {
        // V2 FIX: Scope CartViewModel to the Activity in AppNavigation Compoable

        composable(Route.CustomerHome.route) {
            val customerViewModel: CustomerHomeViewModel = viewModel()
            HomeScreen(navController, authViewModel, customerViewModel, cartViewModel)
        }

        composable(Route.CustomerSearch.route) {
            SearchScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
        composable(
            route = Route.Catalog.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    defaultValue = "" // Empty string means "See All"
                },
                navArgument("categoryName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            CatalogScreen(
                onBackClick = { navController.popBackStack() },
                onMenuItemClick = { menuItemId ->
                    navController.navigate(Route.MenuItemDetail.createRoute(menuItemId))
                },
                onAddToCartClick = { item -> cartViewModel.addItemToCart(item) }
            )
        }

        composable(
            route = Route.RestaurantDetail.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            RestaurantDetailScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Route.Cart.route) },
                viewModel = viewModel(),
                cartViewModel = cartViewModel
            )
        }
        composable(
            route = Route.MenuItemDetail.route,
            arguments = listOf(navArgument("menuItemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: ""
            // We will create MenuItemDetailScreen in the next steps
            MenuItemDetailScreen(
                navController = navController,
                menuItemId = menuItemId,
                onBackClick = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }

        composable(Route.Cart.route) {
            CartScreen(navController, cartViewModel)
        }

        composable(Route.PaymentInstruction.route) {
            PaymentInstructionScreen(navController, cartViewModel)
        }

        composable(Route.CustomerOrders.route) {
            OrdersScreen(navController, authViewModel)
        }

        composable(
            route = Route.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            OrderDetailScreen(navController = navController)
        }

        composable(Route.Profile.route) {
            ProfileScreen(navController, authViewModel)
        }
        composable(Route.Favorites.route) {
            FavoritesScreen(
                onBackClick = { navController.popBackStack() },
                onMenuItemClick = { menuItemId ->
                    navController.navigate(Route.MenuItemDetail.createRoute(menuItemId))
                }
            )
        }
    }
}