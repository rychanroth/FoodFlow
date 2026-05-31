package com.example.foodflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodflow.ui.Route

@Composable
fun CustomerBottomBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Route.CustomerHome.route,
            onClick = {
                if (currentRoute != Route.CustomerHome.route) {
                    navController.navigate(Route.CustomerHome.route) {
                        popUpTo(Route.CustomerHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = currentRoute == Route.CustomerSearch.route,
            onClick = {
                if (currentRoute != Route.CustomerSearch.route) {
                    navController.navigate(Route.CustomerSearch.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = currentRoute == Route.Cart.route,
            onClick = {
                if (currentRoute != Route.Cart.route) {
                    navController.navigate(Route.Cart.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Default.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders") },
            selected = currentRoute == Route.CustomerOrders.route,
            onClick = {
                if (currentRoute != Route.CustomerOrders.route) {
                    navController.navigate(Route.CustomerOrders.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerBottomBarPreview() {
    CustomerBottomBar(
        navController = rememberNavController(),
        currentRoute = Route.CustomerOrders.route
    )
}