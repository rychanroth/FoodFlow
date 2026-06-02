package com.example.foodflow.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.home.DriverHomeScreen
import com.example.foodflow.ui.screens.home.SettingsScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.DriverOrdersViewModel
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
            DriverHomeScreen(authViewModel = authViewModel, driverViewModel = viewModel())
        }
        composable(Route.Settings.route) {
            SettingsScreen(navController, settingsViewModel)
        }
    }
}