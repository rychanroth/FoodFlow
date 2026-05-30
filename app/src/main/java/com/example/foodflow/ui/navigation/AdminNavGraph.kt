package com.example.foodflow.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.home.AdminApplicationScreen

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(
        startDestination = Route.AdminApplications.route, // We need to add this route!
        route = Route.AdminGraph.route
    ) {
        composable(Route.AdminApplications.route) {
            AdminApplicationScreen()
        }
    }
}