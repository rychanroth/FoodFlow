package com.example.foodflow.ui

sealed class Route(val route: String) {
    data object Register : Route("register")
    data object Login : Route("login")
    data object ForgotPassword : Route("forgot_password")

    // Role-based home destinations
    data object CustomerHome : Route("customer_home")
    data object RestaurantHome : Route("restaurant_home")
    data object DriverHome : Route("driver_home")
}