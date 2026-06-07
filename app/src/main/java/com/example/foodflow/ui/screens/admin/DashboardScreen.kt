package com.example.foodflow.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.viewmodel.AdminViewModel
import com.example.foodflow.ui.viewmodel.AuthViewModel

@Composable
fun AdminDashboardScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val adminViewModel: AdminViewModel = viewModel()
    val promotions by adminViewModel.promotions.collectAsState()
    AdminDashboardContent(
        onLogoutClick = { authViewModel.logout() },
        pendingPromotions = promotions.filter { !it.isActive },
        onApprovePromotion = { adminViewModel.approvePromotion(it) },
        onRejectPromotion = { adminViewModel.rejectPromotion(it) },
        onNavigateToCategories = { navController.navigate(Route.AdminCategories.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    onLogoutClick: () -> Unit,
    pendingPromotions: List<Promotion>,
    onApprovePromotion: (Promotion) -> Unit,
    onRejectPromotion: (Promotion) -> Unit,
    onNavigateToCategories: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FoodFlow Admin") },
                actions = {
                    TextButton(onClick = onLogoutClick) { Text("Logout") }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Catalog Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    FilledTonalButton(onClick = onNavigateToCategories) { Text("Categories") }
                }
            }

            if (pendingPromotions.isNotEmpty()) {
                item { Text("Pending Promotions", style = MaterialTheme.typography.titleMedium) }
                items(pendingPromotions) { promo ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(model = promo.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            Spacer(Modifier.height(8.dp))
                            Text("Restaurant ID: ${promo.restaurantId}", style = MaterialTheme.typography.bodySmall)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                OutlinedButton(onClick = { onRejectPromotion(promo) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Reject") }
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = { onApprovePromotion(promo) }) { Text("Approve") }
                            }
                        }
                    }
                }
            }
        }
    }
}