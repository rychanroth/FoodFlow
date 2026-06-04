package com.example.foodflow.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.common.OrderDetailScreen
import com.example.foodflow.ui.screens.driver.HomeScreen
import com.example.foodflow.ui.screens.common.SettingsScreen
import com.example.foodflow.ui.screens.driver.EarningsScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.SettingsViewModel

fun NavGraphBuilder.driverGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
    // FIX: Wrap in navigation block
    navigation(
        startDestination = Route.DriverHome.route,
        route = Route.DriverGraph.route
    ) {
        composable(Route.DriverHome.route) {
            HomeScreen(authViewModel = authViewModel, driverViewModel = viewModel())
        }
        composable(Route.DriverEarnings.route) {
            EarningsScreen(navController, authViewModel)
        }
        composable(
            route = Route.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            OrderDetailScreen(navController = navController)
        }
        composable(Route.Settings.route) {
            SettingsScreen(navController, settingsViewModel)
        }
    }
}