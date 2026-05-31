package com.example.foodflow.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.home.RestaurantHomeScreen
import com.example.foodflow.ui.screens.home.RestaurantOrdersScreen
import com.example.foodflow.ui.screens.home.SettingsScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel
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
            val menuViewModel: MenuViewModel = viewModel()
            RestaurantHomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                menuViewModel = menuViewModel
            )
        }
        composable(Route.RestaurantOrders.route) {
            RestaurantOrdersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Route.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBackClick = {
                    navController.navigate(Route.RestaurantHome.route) {
                        popUpTo(Route.RestaurantHome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}