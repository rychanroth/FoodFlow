package com.example.foodflow.ui.components.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.navigation.Route

@Composable
fun DriverBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(modifier = Modifier, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.DeliveryDining, contentDescription = "Deliveries") },
            label = { Text("Deliveries") },
            selected = currentRoute == Route.DriverHome.route,
            onClick = {
                if (currentRoute != Route.DriverHome.route) {
                    navController.navigate(Route.DriverHome.route) {
                        popUpTo(Route.DriverHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Money, contentDescription = "Earnings") },
            label = { Text("Earnings") },
            selected = currentRoute == Route.DriverEarnings.route,
            onClick = {
                if (currentRoute != Route.DriverEarnings.route) {
                    navController.navigate(Route.DriverEarnings.route) {
                        popUpTo(Route.DriverEarnings.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == Route.Settings.route,
            onClick = {
                if (currentRoute != Route.Settings.route) {
                    navController.navigate(Route.Settings.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}