package com.example.foodflow.ui

sealed class Route(val route: String) {
    // Graph Routes (The Neighborhoods)
    data object AuthGraph : Route("auth_graph")
    data object CustomerGraph : Route("customer_graph")
    data object RestaurantGraph : Route("restaurant_graph")
    data object DriverGraph : Route("driver_graph")
    data object AdminGraph : Route("admin_graph")


    // Auth Screens
    data object Login : Route("login")
    data object Register : Route("register")
    data object ForgotPassword : Route("forgot_password")
    data object Apply : Route("apply")

    // Customer Screens
    data object CustomerHome : Route("customer_home")
    data object CustomerSearch : Route("customer_search")
    data object Cart : Route("cart")
    data object CustomerOrders : Route("customer_orders")
    data object RestaurantDetail : Route("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }

    // Restaurant Screens
    data object RestaurantHome : Route("restaurant_home")
    data object RestaurantOrders : Route("restaurant_orders")

    // Driver Screens
    data object DriverHome : Route("driver_home")

    // Admin Screens
    data object AdminApplications : Route("admin_applications")
}