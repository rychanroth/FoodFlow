package com.example.foodflow.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.auth.ForgotPasswordScreen
import com.example.foodflow.ui.screens.auth.LoginScreen
import com.example.foodflow.ui.screens.auth.RegisterScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel

fun NavGraphBuilder.authGraph(navController: NavController, authViewModel: AuthViewModel) {
    // FIX: Wrap in navigation block
    navigation(
        startDestination = Route.Login.route,
        route = Route.AuthGraph.route
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
    }
}