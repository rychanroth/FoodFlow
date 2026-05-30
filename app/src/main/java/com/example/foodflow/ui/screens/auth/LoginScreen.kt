package com.example.foodflow.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.BuildConfig
import androidx.navigation.NavController
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.components.GoogleSignInButton
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    // Get it from BuildConfig
    val webClientId = BuildConfig.WEB_CLIENT_ID

    // Handle Navigation & Password Reset UI
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Fix: Remove manual navigation logic, only let it handles UI State
            }
            is AuthState.PasswordResetSent -> {
                // We'll just show a toast/snackbar later, for now reset to Idle
                authViewModel.resetState()
            }
            else -> {} // Idle, Loading, Error handled below
        }
    }

    LoginContent(
        authState = authState,
        webClientId = webClientId,
        onGoogleSignInTokenReceived = { idToken ->
            authViewModel.googleSignIn(idToken)
        },
        onGoogleSignInError = { errorMessage ->
            // Show error snackbar or text
        },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        onEmailLoginClick = { email, password ->
            authViewModel.login(email, password) },
        onForgotPasswordClick = { navController.navigate(Route.ForgotPassword.route) },
        onNavigateToRegister = { navController.navigate(Route.Register.route) }
    )
}

@Composable
fun LoginContent(
    authState: AuthState,
    webClientId: String,
    onGoogleSignInTokenReceived: (String) -> Unit,
    onGoogleSignInError: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onEmailLoginClick: (String, String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Google Sign-In Button
        GoogleSignInButton(
            webClientId = webClientId,
            onTokenReceived = onGoogleSignInTokenReceived,
            onError = onGoogleSignInError
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("OR", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        // Email and Password Sign-In section
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
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onEmailLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onForgotPasswordClick) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Register
        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Register")
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (authState is AuthState.PasswordResetSent) {
            Text(
                text = "Password reset email sent!",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}