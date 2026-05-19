package com.example.foodflow

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.auth.RegisterScreen
import com.example.foodflow.ui.screens.home.CustomerHomeScreen
import com.example.foodflow.ui.screens.home.DriverHomeScreen
import com.example.foodflow.ui.screens.home.RestaurantHomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Register.route,
        modifier = modifier
    ) {
        composable(Route.Register.route) {
            RegisterScreen(navController)
        }

        composable(Route.CustomerHome.route) {
            CustomerHomeScreen()
        }

        composable(Route.RestaurantHome.route) {
            RestaurantHomeScreen()
        }

        composable(Route.DriverHome.route) {
            DriverHomeScreen()
        }
    }
}