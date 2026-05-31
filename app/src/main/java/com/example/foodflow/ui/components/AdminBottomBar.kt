package com.example.foodflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.Route

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
    }
}