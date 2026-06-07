package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.BuildConfig
import com.example.foodflow.ui.components.auth.GoogleSignInButton
import com.example.foodflow.ui.components.common.FoodFlowLoadingButton
import com.example.foodflow.ui.components.common.FoodFlowPasswordField
import com.example.foodflow.ui.components.common.FoodFlowTextField
import com.example.foodflow.ui.components.customer.AwaitingVerificationCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val webClientId = BuildConfig.WEB_CLIENT_ID

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {}
            is AuthState.PasswordResetSent -> { authViewModel.resetState() }
            else -> {}
        }
    }

    if (authState is AuthState.AwaitingVerification) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AwaitingVerificationCard { authViewModel.resetState() }
        }
    } else {
        LoginContent(
            authState = authState,
            webClientId = webClientId,
            onGoogleSignInTokenReceived = { idToken -> authViewModel.googleSignIn(idToken) },
            onGoogleSignInError = { },
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            onEmailLoginClick = { email, password -> authViewModel.login(email, password) },
            onForgotPasswordClick = { navController.navigate(Route.ForgotPassword.route) },
            onNavigateToRegister = { navController.navigate(Route.Register.route) }
        )
    }
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

        GoogleSignInButton(
            webClientId = webClientId,
            onTokenReceived = onGoogleSignInTokenReceived,
            onError = onGoogleSignInError
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("OR", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        FoodFlowTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(8.dp))

        FoodFlowPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            imeAction = ImeAction.Done
        )
        Spacer(modifier = Modifier.height(16.dp))

        FoodFlowLoadingButton(
            text = "Login",
            onClick = { onEmailLoginClick(email, password) },
            isLoading = authState is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onForgotPasswordClick) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

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