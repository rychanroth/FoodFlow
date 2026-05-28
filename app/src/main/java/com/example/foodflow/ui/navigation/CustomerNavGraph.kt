package com.example.foodflow.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.home.CartScreen
import com.example.foodflow.ui.screens.home.CustomerHomeScreen
import com.example.foodflow.ui.screens.home.CustomerOrdersScreen
import com.example.foodflow.ui.screens.home.CustomerSearchScreen
import com.example.foodflow.ui.screens.home.RestaurantDetailScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.CustomerHomeViewModel
import com.example.foodflow.ui.viewmodel.CustomerOrdersViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDetailViewModel
import com.google.firebase.auth.FirebaseAuth

fun NavGraphBuilder.customerGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // FIX: Wrap in navigation block
    navigation(
        startDestination = Route.CustomerHome.route,
        route = Route.CustomerGraph.route
    ) {
        composable(Route.CustomerHome.route) {
            val customerViewModel: CustomerHomeViewModel = viewModel()
            val cartViewModel: CartViewModel = viewModel()
            CustomerHomeScreen(navController, authViewModel, customerViewModel, cartViewModel)
        }

        composable(Route.CustomerSearch.route) {
            CustomerSearchScreen(navController = navController)
        }

        composable(
            route = Route.RestaurantDetail.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            val cartViewModel: CartViewModel = viewModel()
            RestaurantDetailScreen(
                restaurantId = restaurantId,
                restaurantName = "Restaurant Menu",
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Route.Cart.route) },
                viewModel = viewModel(),
                cartViewModel = cartViewModel
            )
        }

        composable(Route.Cart.route) {
            val cartViewModel: CartViewModel = viewModel()
            val cartItems by cartViewModel.cartItems.collectAsState()
            val totalPrice = cartViewModel.getTotalPrice()
            val currentUser = FirebaseAuth.getInstance().currentUser

            CartScreen(
                cartItems = cartItems,
                totalPrice = totalPrice,
                onBackClick = { navController.popBackStack() },
                onIncreaseClick = { cartViewModel.increaseQuantity(it) },
                onDecreaseClick = { cartViewModel.decreaseQuantity(it) },
                onCheckoutClick = {
                    if (currentUser != null) {
                        cartViewModel.placeOrder(currentUser.uid)
                        navController.navigate(Route.CustomerHome.route) {
                            popUpTo(Route.CustomerHome.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Route.CustomerOrders.route) {
            val customerOrdersViewModel: CustomerOrdersViewModel = viewModel()
            val currentUser = FirebaseAuth.getInstance().currentUser

            LaunchedEffect(currentUser) {
                currentUser?.uid?.let { customerOrdersViewModel.loadOrders(it) }
            }

            val orders by customerOrdersViewModel.orders.collectAsState()

            CustomerOrdersScreen(
                orders = orders,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}