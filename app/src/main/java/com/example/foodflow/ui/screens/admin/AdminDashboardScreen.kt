package com.example.foodflow.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
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
    val pendingApps by adminViewModel.pendingApps.collectAsState()

    // NEW: Stats
    val todayRevenue by adminViewModel.todayRevenue.collectAsState()
    val todayOrders by adminViewModel.todayOrders.collectAsState()
    val totalUsers by adminViewModel.totalUsers.collectAsState()

    AdminDashboardContent(
        todayRevenue = todayRevenue,
        todayOrders = todayOrders,
        totalUsers = totalUsers,
        pendingAppsCount = pendingApps.size,
        pendingPromotions = promotions.filter { !it.isActive && !it.isRejected },
        onLogoutClick = { authViewModel.logout() },
        onApprovePromotion = { adminViewModel.approvePromotion(it) },
        onRejectPromotion = { adminViewModel.rejectPromotion(it) },
        onNavigateToCategories = { navController.navigate(Route.AdminCategories.route) },
        onNavigateToApplications = { navController.navigate(Route.AdminApplications.route) } // NEW
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    todayRevenue: Double,
    todayOrders: Int,
    totalUsers: Int,
    pendingAppsCount: Int,
    pendingPromotions: List<Promotion>,
    onLogoutClick: () -> Unit,
    onApprovePromotion: (Promotion) -> Unit,
    onRejectPromotion: (Promotion) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToApplications: () -> Unit
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
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECTION 1: PLATFORM INSIGHTS ---
            item {
                Text("Today's Insights", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        title = "Revenue",
                        value = "$${String.format("%.2f", todayRevenue)}",
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    DashboardCard(
                        title = "Orders",
                        value = todayOrders.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    DashboardCard(
                        title = "Users",
                        value = totalUsers.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // --- SECTION 2: QUICK ACTIONS ---
            item {
                Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pending Applications Card
                    Card(
                        modifier = Modifier.weight(1f).clickable { onNavigateToApplications() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Badge(containerColor = MaterialTheme.colorScheme.error) { Text(pendingAppsCount.toString()) }
                            Spacer(Modifier.height(4.dp))
                            Text("Pending Apps", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    // Catalog Management Card
                    Card(
                        modifier = Modifier.weight(1f).clickable { onNavigateToCategories() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text("Categories", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // --- SECTION 3: PENDING PROMOTIONS ---
            if (pendingPromotions.isNotEmpty()) {
                item {
                    Text("Pending Promotions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                }
                items(pendingPromotions) { promo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = promo.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Restaurant ID: ${promo.restaurantId}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { onRejectPromotion(promo) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) { Text("Reject") }
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

// Reusable Dashboard Card Component
@Composable
private fun DashboardCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}