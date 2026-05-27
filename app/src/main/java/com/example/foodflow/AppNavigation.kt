package com.example.foodflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.screens.auth.ForgotPasswordScreen
import com.example.foodflow.ui.screens.auth.LoginScreen
import com.example.foodflow.ui.screens.auth.RegisterScreen
import com.example.foodflow.ui.screens.home.CartScreen
import com.example.foodflow.ui.screens.home.CustomerHomeScreen
import com.example.foodflow.ui.screens.home.DriverHomeScreen
import com.example.foodflow.ui.screens.home.RestaurantDetailScreen
import com.example.foodflow.ui.screens.home.RestaurantHomeScreen
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.CartViewModel
import com.example.foodflow.ui.viewmodel.CustomerHomeViewModel
import com.example.foodflow.ui.viewmodel.MenuViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.pipeline.Expression.Companion.type

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    menuViewModel: MenuViewModel = viewModel(),
    customerViewModel: CustomerHomeViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()

    val startDestination = when {
        authState is AuthState.Success -> {
            when ((authState as AuthState.Success).role) {
                UserRole.RESTAURANT -> Route.RestaurantHome.route
                UserRole.DRIVER -> Route.DriverHome.route
                else -> Route.CustomerHome.route
            }
        }
        else -> Route.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Route.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Route.ForgotPassword.route) {
            ForgotPasswordScreen(navController, authViewModel)
        }
        composable(Route.CustomerHome.route) {
            CustomerHomeScreen(
                navController,
                authViewModel,
                customerViewModel,
                cartViewModel
            )
        }
        composable(Route.RestaurantHome.route) {
            RestaurantHomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                menuViewModel = menuViewModel
            )
        }
        composable(Route.DriverHome.route) {
            DriverHomeScreen(
            )
        }
        composable(
            route = Route.RestaurantDetail.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.StringType }
            )
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
    }
}