package com.example.foodflow.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.home.AdminApplicationScreen
import com.example.foodflow.ui.screens.home.AdminDashboardScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel

fun NavGraphBuilder.adminGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Route.AdminDashboard.route,
        route = Route.AdminGraph.route
    ) {
        composable(Route.AdminDashboard.route) {
            AdminDashboardScreen(authViewModel)
        }
        composable(Route.AdminApplications.route) {
            AdminApplicationScreen()
        }
    }
}