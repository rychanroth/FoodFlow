package com.example.foodflow

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.auth.ForgotPasswordScreen
import com.example.foodflow.ui.screens.auth.LoginScreen
import com.example.foodflow.ui.screens.auth.RegisterScreen
import com.example.foodflow.ui.screens.home.CustomerHomeScreen
import com.example.foodflow.ui.screens.home.DriverHomeScreen
import com.example.foodflow.ui.screens.home.RestaurantHomeScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    menuViewModel: MenuViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.route,
        modifier = modifier
    ) {
        composable(Route.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Route.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Route.ForgotPassword.route) {
            ForgotPasswordScreen(navController, authViewModel)
        }

        composable(Route.CustomerHome.route) {
            CustomerHomeScreen()
        }

        composable(Route.RestaurantHome.route) {
            RestaurantHomeScreen(menuViewModel, authViewModel, navController)
        }

        composable(Route.DriverHome.route) {
            DriverHomeScreen()
        }
    }
}