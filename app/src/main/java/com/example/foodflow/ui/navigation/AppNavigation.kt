package com.example.foodflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    // Determine start destination based on Auth State
    val startDestination = when (authState) {
        is AuthState.Success -> {
            when ((authState as AuthState.Success).role) {
                UserRole.RESTAURANT -> Route.RestaurantGraph.route
                UserRole.DRIVER -> Route.DriverGraph.route
                else -> Route.CustomerGraph.route
            }
        }
        else -> Route.AuthGraph.route
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
        else -> {
            { } // No bottom bar for Auth graph
        }
    }

    Scaffold(
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
        }
    }
}