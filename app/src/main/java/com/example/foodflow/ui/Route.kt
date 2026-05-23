package com.example.foodflow.ui

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object Register : Route("register")
    data object ForgotPassword : Route("forgot_password")

    data object CustomerHome : Route("customer_home")
    data object RestaurantHome : Route("restaurant_home")
    data object DriverHome : Route("driver_home")

    data object RestaurantDetail : Route("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }

    data object Cart : Route("cart")
}