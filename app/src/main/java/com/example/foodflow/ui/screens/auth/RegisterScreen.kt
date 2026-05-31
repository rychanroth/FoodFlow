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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.components.AwaitingVerificationCard
import com.example.foodflow.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    // Hoisted State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // NEW

    // Local validation error state
    var localError by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Fix: Remove manual navigation logic, only let it handles UI State
        }
    }

    // Inside LoginScreen Column UI
    if (authState is AuthState.AwaitingVerification) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AwaitingVerificationCard { authViewModel.resetState() }
        }
    } else {
        RegisterContent(
            authState = authState,
            localError = localError, // Pass local error down
            email = email,
            onEmailChange = { email = it; localError = null },
            password = password,
            onPasswordChange = { password = it; localError = null },
            confirmPassword = confirmPassword, // NEW
            onConfirmPasswordChange = { confirmPassword = it; localError = null }, // NEW
            onRegister = {
                // V2 UX: Validate locally before hitting Firebase
                if (password != confirmPassword) {
                    localError = "Passwords do not match"
                } else if (password.length < 6) {
                    localError = "Password must be at least 6 characters"
                } else {
                    authViewModel.register(email, password)
                }
            },
            onNavigateToLogin = { navController.navigate(Route.Login.route) }
        )
    }
}

// Fix: Rendering Problem
// Problem: You can't preview a function if it has viewModel as the parameter
// because viewModel.Firebase related instances are not initialized yet

@Composable
fun RegisterContent(
    authState: AuthState,
    localError: String?, // NEW
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String, // NEW
    onConfirmPasswordChange: (String) -> Unit, // NEW
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // NEW Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
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

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }

        // Error Handling (Prioritize local errors, then ViewModel errors)
        val errorMessage = localError ?: (authState as? AuthState.Error)?.message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
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
        onRegister = { -> },
        onNavigateToLogin = {},
        localError = TODO(),
        email = TODO(),
        onEmailChange = TODO(),
        password = TODO(),
        onPasswordChange = TODO(),
        confirmPassword = TODO(),
        onConfirmPasswordChange = TODO(),
    )
}