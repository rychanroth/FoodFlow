package com.example.foodflow.ui.components.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
fun AdminBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(modifier = Modifier, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentRoute == Route.AdminDashboard.route,
            onClick = {
                if (currentRoute != Route.AdminDashboard.route) {
                    navController.navigate(Route.AdminDashboard.route) {
                        popUpTo(Route.AdminDashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Default.Assignment, contentDescription = "Applications") },
            label = { Text("Applications") },
            selected = currentRoute == Route.AdminApplications.route,
            onClick = {
                if (currentRoute != Route.AdminApplications.route) {
                    navController.navigate(Route.AdminApplications.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == Route.AdminSettings.route,
            onClick = {
                if (currentRoute != Route.AdminSettings.route) {
                    navController.navigate(Route.AdminSettings.route) {
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