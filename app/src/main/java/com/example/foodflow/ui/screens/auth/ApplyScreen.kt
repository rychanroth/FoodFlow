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
import androidx.navigation.NavController
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.viewmodel.ApplicationViewModel
import com.example.foodflow.ui.viewmodel.ApplyState
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ApplyScreen(
    navController: NavController,
    viewModel: ApplicationViewModel = viewModel()
) {
    // Extract the state flow at the very top
    val applyState by viewModel.applyState.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid
    val currentUserEmail = currentUser?.email ?: ""

    ApplyContent(
        applyState = applyState,
        onBackClick = { navController.popBackStack() },
        onSubmitClick = { role, details ->
            if (currentUserId != null) {
                viewModel.submitApplication(currentUserId, currentUserEmail, role, details)
            } else {
                navController.popBackStack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyContent(
    applyState: ApplyState,
    onBackClick: () -> Unit,
    onSubmitClick: (UserRole, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // UI Form states are perfectly fine to keep local inside the stateless layout!
    var selectedRole by remember { mutableStateOf(UserRole.DRIVER) }
    var businessDetails by remember { mutableStateOf("") }

    val isLoading = applyState is ApplyState.Loading

    // Scaffold correctly anchors the TopAppBar to the top of the screen
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Apply for Role") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // The main content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (applyState is ApplyState.Success) {
                // SUCCESS STATE VIEW
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
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
                // FORM STATE VIEW
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedRole == role),
                                    enabled = !isLoading, // Disable swapping while loading
                                    onClick = { selectedRole = role }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedRole == role),
                                onClick = null, // Handled by the Row's selectable modifier
                                enabled = !isLoading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    enabled = !isLoading // Lock text input while submitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSubmitClick(selectedRole, businessDetails) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && businessDetails.isNotBlank() // Prevent empty submissions
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Submit Application")
                    }
                }

                // Error Handling
                if (applyState is ApplyState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = applyState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}