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
    val currentRoute = navBackStackEntry?.destination?.route

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

    // Determine which Bottom Bar to show based on the current graph
    val bottomBar: @Composable () -> Unit = when {
        currentRoute?.startsWith(Route.CustomerGraph.route) == true -> {
            { CustomerBottomBar(navController = navController, currentRoute = currentRoute) }
        }
        currentRoute?.startsWith(Route.RestaurantGraph.route) == true -> {
            { RestaurantBottomBar(navController = navController, currentRoute = currentRoute) }
        }
        currentRoute?.startsWith(Route.DriverGraph.route) == true -> {
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