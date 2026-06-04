package com.example.foodflow.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.Application
import com.example.foodflow.ui.viewmodel.AdminViewModel

// ... imports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import com.example.foodflow.ui.components.customer.ApplicationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    viewModel: AdminViewModel = viewModel()
) {
    val pendingApps by viewModel.pendingApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applications") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            if (pendingApps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pending applications.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(pendingApps, key = { it.id }) { app ->
                        ApplicationCard(
                            app = app,
                            onApprove = { viewModel.approveApplication(app) },
                            onReject = { viewModel.rejectApplication(app.id) }
                        )
                    }
                }
            }
        }
    }
}