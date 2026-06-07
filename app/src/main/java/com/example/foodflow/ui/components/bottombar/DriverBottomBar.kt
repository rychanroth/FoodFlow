package com.example.foodflow.ui.components.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
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