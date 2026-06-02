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
import com.example.foodflow.data.model.CheckoutState
import com.example.foodflow.data.model.PaymentMethod
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.auth.ApplyScreen
import com.example.foodflow.ui.screens.home.CartScreen
import com.example.foodflow.ui.screens.home.CustomerHomeScreen
import com.example.foodflow.ui.screens.home.CustomerOrdersScreen
import com.example.foodflow.ui.screens.home.CustomerSearchScreen
import com.example.foodflow.ui.screens.home.PaymentInstructionScreen
import com.example.foodflow.ui.screens.home.ProfileScreen
import com.example.foodflow.ui.screens.home.RestaurantDetailScreen
import com.example.foodflow.ui.screens.home.SettingsScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.CustomerHomeViewModel
import com.example.foodflow.ui.viewmodel.CustomerOrdersViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDetailViewModel
import com.example.foodflow.ui.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth

fun NavGraphBuilder.customerGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    settingsViewModel: SettingsViewModel
) {
    // FIX: Wrap in navigation block
    navigation(
        startDestination = Route.CustomerHome.route,
        route = Route.CustomerGraph.route
    ) {
        // V2 FIX: Scope CartViewModel to the Activity in AppNavigation Compoable

        composable(Route.CustomerHome.route) {
            val customerViewModel: CustomerHomeViewModel = viewModel()
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
            CartScreen(navController, cartViewModel)
        }

        composable(Route.PaymentInstruction.route) {
            val settings by cartViewModel.settings.collectAsState()
            // Get the orderId from the ViewModel
            val orderId = cartViewModel.lastOrderId

            if (orderId != null) {
                PaymentInstructionScreen(
                    orderId = orderId,
                    totalAmount = cartViewModel.lastOrderTotal,
                    bankAccountDetails = settings.platformBankAccount,
                    bankPaymentUrl = settings.platformBankAccountUrl,
                    cartViewModel = cartViewModel, // Pass ViewModel for upload
                    onPaymentConfirmed = {
                        navController.navigate(Route.CustomerHome.route) {
                            popUpTo(Route.CustomerHome.route) { inclusive = true }
                        }
                    },
                    // By the time the user reaches the Payment screen, cartViewModel.clearCart() has already run
                    // Navigate them to Home Screen instead
                    onBackClick = {
                        navController.navigate(Route.CustomerHome.route) {
                            popUpTo(Route.CustomerHome.route) { inclusive = true }
                        }
                    }
                )
            }
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


        composable(Route.Apply.route) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                ApplyScreen(
                    currentUserId = currentUser.uid,
                    currentUserEmail = currentUser.email ?: "",
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(Route.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.Profile.route) {
            ProfileScreen(onBackClick = { navController.popBackStack() })
        }
    }
}