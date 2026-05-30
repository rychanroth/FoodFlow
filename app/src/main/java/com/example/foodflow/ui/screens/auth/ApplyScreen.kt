package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.viewmodel.ApplicationViewModel
import com.example.foodflow.ui.viewmodel.ApplyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    currentUserId: String,
    currentUserEmail: String,
    onBackClick: () -> Unit,
    viewModel: ApplicationViewModel = viewModel()
) {
    val applyState by viewModel.applyState.collectAsState()

    var selectedRole by remember { mutableStateOf(UserRole.DRIVER) } // Default to Driver
    var businessDetails by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Apply for Role") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (applyState is ApplyState.Success) {
            // SUCCESS STATE
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Application Submitted!", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Our team will review your application. You will gain access once approved.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBackClick) {
                        Text("Back to Home")
                    }
                }
            }
        } else {
            // FORM STATE
            Text("Which role are you applying for?", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Role Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val roles = listOf(UserRole.DRIVER, UserRole.RESTAURANT)
                roles.forEach { role ->
                    Row(
                        modifier = Modifier.selectable(
                            selected = (selectedRole == role),
                            onClick = { selectedRole = role }
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedRole == role),
                            onClick = null
                        )
                        Text(role.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = businessDetails,
                onValueChange = { businessDetails = it },
                label = {
                    Text(if (selectedRole == UserRole.DRIVER) "Vehicle details / Experience" else "Restaurant name / Cuisine type")
                },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.submitApplication(currentUserId, currentUserEmail, selectedRole, businessDetails) },
                modifier = Modifier.fillMaxWidth(),
                enabled = applyState !is ApplyState.Loading
            ) {
                if (applyState is ApplyState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit Application")
                }
            }

            if (applyState is ApplyState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (applyState as ApplyState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}