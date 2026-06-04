package com.example.foodflow.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.restaurant.MenuManagementScreen
import com.example.foodflow.ui.screens.restaurant.OrdersScreen
import com.example.foodflow.ui.screens.common.SettingsScreen
import com.example.foodflow.ui.screens.restaurant.HomeScreen
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
            HomeScreen(
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
            OrdersScreen(navController)
        }
        composable(Route.Settings.route) {
            SettingsScreen(navController, settingsViewModel)
        }
    }
}