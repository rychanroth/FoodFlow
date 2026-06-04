package com.example.foodflow.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.customer.ApplyScreen
import com.example.foodflow.ui.screens.customer.CartScreen
import com.example.foodflow.ui.screens.customer.HomeScreen
import com.example.foodflow.ui.screens.customer.OrdersScreen
import com.example.foodflow.ui.screens.customer.SearchScreen
import com.example.foodflow.ui.screens.customer.PaymentInstructionScreen
import com.example.foodflow.ui.screens.common.ProfileScreen
import com.example.foodflow.ui.screens.customer.RestaurantDetailScreen
import com.example.foodflow.ui.screens.common.SettingsScreen
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
            SearchScreen(navController = navController)
        }

        composable(
            route = Route.RestaurantDetail.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            RestaurantDetailScreen(
                restaurantId = restaurantId,
                restaurantName = "Restaurant Menu",
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Route.Cart.route) },
                viewModel = viewModel(),
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
            OrdersScreen(navController)
        }


        composable(Route.Apply.route) {
            ApplyScreen(navController)
        }

        composable(Route.Settings.route) {
            SettingsScreen(navController, settingsViewModel)
        }

        composable(Route.Profile.route) {
            ProfileScreen(navController)
        }
    }
}