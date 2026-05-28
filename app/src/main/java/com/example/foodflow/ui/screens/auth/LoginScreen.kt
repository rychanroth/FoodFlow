package com.example.foodflow.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.AuthState
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.Route
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()


    // 2. Create the Google Sign-In Client
    val googleSignInClient = remember { GoogleSignIn.getClient(context, googleSignInOptions) }

    // 3. Register the Activity Result Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    // SUCCESS: Send token to ViewModel
                    authViewModel.googleSignIn(idToken)
                } else {
                    authViewModel._authState.value = AuthState.Error("Google token was null")
                }
            } catch (e: ApiException) {
                authViewModel._authState.value = AuthState.Error("Google sign-in failed: ${e.message}")
            }
        } else {
            // User cancelled the Google pop-up
            authViewModel._authState.value = AuthState.Idle
        }
    }

    // Handle Navigation & Password Reset UI
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val successState = authState as AuthState.Success
                val destination = when (successState.role) {
                    UserRole.CUSTOMER -> Route.CustomerHome.route
                    UserRole.RESTAURANT -> Route.RestaurantHome.route
                    UserRole.DRIVER -> Route.DriverHome.route
                }
                navController.navigate(destination) {
                    popUpTo(Route.Login.route) { inclusive = true }
                }
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
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        onEmailLoginClick = { email, password ->
            authViewModel.login(email, password) },
        onGoogleLoginClick = {
            // Launch the Google pop-up!
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        },
        onForgotPasswordClick = { navController.navigate(Route.ForgotPassword.route) },
        onNavigateToRegister = { navController.navigate(Route.Register.route) }
    )
}

@Composable
fun LoginContent(
    authState: AuthState,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onEmailLoginClick: (String, String) -> Unit,
    onGoogleLoginClick: () -> Unit,
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