package com.example.foodflow.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.components.customer.FoodCard
import com.example.foodflow.ui.components.customer.RestaurantCard
import com.example.foodflow.ui.viewmodel.CustomerSearchViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: CustomerSearchViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val filteredRestaurants by viewModel.filteredRestaurants.collectAsState()

    CustomerSearchContent(
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        filteredItems = filteredItems,
        filteredRestaurants = filteredRestaurants,
        onBackClick = { navController.popBackStack() },
        onRestaurantClick = { restaurantId ->
            navController.navigate(Route.RestaurantDetail.createRoute(restaurantId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSearchContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredItems: List<MenuItem>,
    filteredRestaurants: List<AppUser>,
    onBackClick: () -> Unit,
    onRestaurantClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search food or restaurants") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Start typing to search...")
            }
        } else if (filteredItems.isEmpty() && filteredRestaurants.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No results found for '$searchQuery'")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                if (filteredItems.isNotEmpty()) {
                    item { Text("Menu Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                    items(filteredItems) { item ->
                        FoodCard(item = item)
                    }
                }

                if (filteredRestaurants.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Restaurants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(filteredRestaurants) { restaurant ->
                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.uid) }
                        )
                    }
                }
            }
        }
    }
}