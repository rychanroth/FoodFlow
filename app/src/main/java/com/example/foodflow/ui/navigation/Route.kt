package com.example.foodflow.ui.navigation

sealed class Route(val route: String) {
    // Graph Routes (The Neighborhoods)
    data object AuthGraph : Route("auth_graph")
    data object CustomerGraph : Route("customer_graph")
    data object RestaurantGraph : Route("restaurant_graph")
    data object DriverGraph : Route("driver_graph")
    data object AdminGraph : Route("admin_graph")

    // Global Screens
    data object Settings : Route("settings")
    data object Profile : Route("profile")
    data object Onboarding : Route("onboarding") // NEW V3


    // Auth Screens
    data object Login : Route("login")
    data object Register : Route("register")
    data object ForgotPassword : Route("forgot_password")

    // Customer Screens
    data object CustomerHome : Route("customer_home")
    data object CustomerSearch : Route("customer_search")
    data object Cart : Route("cart")
    data object PaymentInstruction : Route("payment_instruction")
    data object CustomerOrders : Route("customer_orders")
    data object RestaurantDetail : Route("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }
    data object Apply : Route("apply")

    // Restaurant Screens
    data object RestaurantHome : Route("restaurant_home")
    data object RestaurantMenuManagement : Route("restaurant_menu_management")
    data object RestaurantOrders : Route("restaurant_orders")

    // Driver Screens
    data object DriverHome : Route("driver_home")
    object DriverEarnings : Route("driver_earnings")

    // Admin Screens
    data object AdminDashboard : Route("admin_dashboard")
    data object AdminApplications : Route("admin_applications")
    data object AdminSettings : Route("admin_settings")

    // DETAIL SCREEN
    object OrderDetail : Route("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
}