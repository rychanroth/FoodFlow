package com.example.foodflow.ui.components.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.Route

@Composable
fun RestaurantBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(modifier = Modifier, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
            label = { Text("Menu") },
            selected = currentRoute == Route.RestaurantHome.route,
            onClick = {
                if (currentRoute != Route.RestaurantHome.route) {
                    navController.navigate(Route.RestaurantHome.route) {
                        popUpTo(Route.RestaurantHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders") },
            selected = currentRoute == Route.RestaurantOrders.route,
            onClick = {
                if (currentRoute != Route.RestaurantOrders.route) {
                    navController.navigate(Route.RestaurantOrders.route) {
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