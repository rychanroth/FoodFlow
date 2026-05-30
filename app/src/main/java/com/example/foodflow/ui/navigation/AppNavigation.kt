package com.example.foodflow.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.components.CustomerBottomBar
import com.example.foodflow.ui.components.DriverBottomBar
import com.example.foodflow.ui.components.RestaurantBottomBar
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Get the current screen route
    val currentRoute = navBackStackEntry?.destination?.route
    // Get the parent graph route (e.g., "customer_graph")
    val currentGraphRoute = navBackStackEntry?.destination?.parent?.route

    // Determine start destination based purely on Auth State
    val startDestination = when (authState) {
        is AuthState.Success -> {
            val role = (authState as AuthState.Success).role
            when (role) {
                UserRole.CUSTOMER -> Route.CustomerGraph.route
                UserRole.RESTAURANT -> Route.RestaurantGraph.route
                UserRole.DRIVER -> Route.DriverGraph.route
                UserRole.ADMIN -> Route.AdminGraph.route
                else -> Route.CustomerGraph.route
            }
        }
        is AuthState.Loading -> null // We don't know yet, show a spinner
        else -> Route.AuthGraph.route // Idle, Error, PasswordReset -> Show Auth
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return // Don't build the NavHost yet
    }

    // Determine which Bottom Bar to show based on the PARENT GRAPH
    val bottomBar: @Composable () -> Unit = when (currentGraphRoute) {
        Route.CustomerGraph.route -> {
            { CustomerBottomBar(navController = navController, currentRoute = currentRoute) }
        }
        Route.RestaurantGraph.route -> {
            { RestaurantBottomBar(navController = navController, currentRoute = currentRoute) }
        }
        Route.DriverGraph.route -> {
            { DriverBottomBar(navController = navController, currentRoute = currentRoute) }
        }
        Route.AdminGraph.route -> {
            { /*TODO: */ }
        }
        else -> {
            { } // No bottom bar for Auth graph
        }
    }

    Scaffold(
        // FIX: remove window insets on parent host
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = bottomBar
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
        ) {
            // Delegating all screen routing to their respective graphs!
            authGraph(navController, authViewModel)
            customerGraph(navController, authViewModel)
            restaurantGraph(navController, authViewModel)
            driverGraph(navController, authViewModel)
            adminGraph(navController)
        }
    }
}