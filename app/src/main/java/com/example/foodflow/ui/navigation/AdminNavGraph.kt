package com.example.foodflow.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.screens.admin.AdminCategoryScreen
import com.example.foodflow.ui.screens.admin.AdminDashboardScreen
import com.example.foodflow.ui.screens.admin.AdminOrdersScreen
import com.example.foodflow.ui.screens.admin.ApplicationScreen
import com.example.foodflow.ui.screens.admin.SettingsScreen
import com.example.foodflow.ui.screens.common.ProfileScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.SettingsViewModel

fun NavGraphBuilder.adminGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
    navigation(
        startDestination = Route.AdminDashboard.route,
        route = Route.AdminGraph.route
    ) {
        composable(Route.AdminDashboard.route) {
            AdminDashboardScreen(navController, authViewModel)
        }
        composable(Route.AdminApplications.route) {
            ApplicationScreen(navController)
        }
        composable(Route.AdminCategories.route) {
            AdminCategoryScreen(navController)
        }
        composable(Route.AdminOrders.route) {
            AdminOrdersScreen(navController = navController)
        }
        composable(Route.AdminSettings.route) {
            SettingsScreen()
        }
        composable(Route.Profile.route) {
            ProfileScreen(navController, authViewModel)
        }
    }
}