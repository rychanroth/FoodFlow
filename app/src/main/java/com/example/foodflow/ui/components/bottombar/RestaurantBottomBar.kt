package com.example.foodflow.ui.components.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.navigation.Route

@Composable
fun RestaurantBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(modifier = Modifier, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
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
            icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
            label = { Text("Menu") },
            selected = currentRoute == Route.RestaurantMenuManagement.route,
            onClick = {
                if (currentRoute != Route.RestaurantMenuManagement.route) {
                    navController.navigate(Route.RestaurantMenuManagement.route) {
                        popUpTo(Route.RestaurantMenuManagement.route) { inclusive = true }
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
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == Route.Profile.route,
            onClick = {
                if (currentRoute != Route.Profile.route) {
                    navController.navigate(Route.Profile.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}