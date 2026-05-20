package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val successState = authState as AuthState.Success
            val destination = when (successState.role) {
                UserRole.CUSTOMER -> Route.CustomerHome.route
                UserRole.RESTAURANT -> Route.RestaurantHome.route
                UserRole.DRIVER -> Route.DriverHome.route
            }

            navController.navigate(destination) {
                popUpTo(Route.Register.route) { inclusive = true }
            }
        }
    }

    RegisterContent(
        authState = authState,
        onRegister = { email, password, role ->
            authViewModel.register(email, password, role)
        }
    )
}

// Fix: Rendering Problem
// Problem: You can't preview a function if it has viewModel as the parameter
// because viewModel.Firebase related instances are not initialized yet

@Composable
fun RegisterContent(
    authState: AuthState,
    onRegister: (String, String, UserRole) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    val roles = UserRole.entries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Role Selector
        Text("I am a:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            roles.forEach { role ->
                Row(
                    modifier = Modifier.selectable(
                        selected = (selectedRole == role),
                        onClick = { selectedRole = role }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        (selectedRole == role), null
                        // Null because the Row handles the click
                    )
                    Text(role.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Button & State Handling
        Button(
            onClick = { onRegister(email, password, selectedRole) },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading // Disable while loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }

        // Error Handling
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = authState.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterContent(
        authState = AuthState.Idle,
        onRegister = { _, _, _ -> }
    )
}