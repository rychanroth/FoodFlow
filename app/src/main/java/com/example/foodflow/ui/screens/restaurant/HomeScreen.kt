package com.example.foodflow.ui.screens.restaurant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.RestaurantDashboardViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel
) {
    val dashboardViewModel: RestaurantDashboardViewModel = viewModel()

    val user by authViewModel.currentUser.collectAsState()
    val restaurantId = user?.uid

    if (restaurantId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        RestaurantDashboardScreen(
            restaurantId = restaurantId,
            viewModel = dashboardViewModel
        )
    }
}