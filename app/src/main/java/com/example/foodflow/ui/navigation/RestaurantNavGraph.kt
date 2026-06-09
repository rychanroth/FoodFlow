package com.example.foodflow.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.screens.restaurant.MenuManagementScreen
import com.example.foodflow.ui.screens.restaurant.RestaurantDashboardScreen
import com.example.foodflow.ui.screens.restaurant.RestaurantOrdersScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.SettingsViewModel

fun NavGraphBuilder.restaurantGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
    // FIX: Wrap in navigation block
    navigation(
        startDestination = Route.RestaurantHome.route,
        route = Route.RestaurantGraph.route
    ) {
        composable(Route.RestaurantHome.route) {
            RestaurantDashboardScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Route.RestaurantMenuManagement.route) {
            MenuManagementScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Route.RestaurantOrders.route) {
            RestaurantOrdersScreen(navController, authViewModel)
        }
    }
}