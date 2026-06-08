package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
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
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Branded Header ──
        // Uses your Theme's displayLarge or headlineLarge (Playfair Display)
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Uses your Theme's bodyMedium (Open Sans)
        Text(
            text = "Sign in to continue ordering your favorites",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Social Login ──
        GoogleSignInButton(
            webClientId = webClientId,
            onTokenReceived = onGoogleSignInTokenReceived,
            onError = onGoogleSignInError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Visual Divider ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                text = "  OR  ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Input Fields ──
        // Assuming FoodFlowTextField uses MaterialTheme.typography internally (Open Sans)
        FoodFlowTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(12.dp))

        FoodFlowPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            imeAction = ImeAction.Done
        )

        // ── Forgot Password (Align End) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPasswordClick, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary // Brand Red
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Primary CTA ──
        // FoodFlowLoadingButton should automatically use primary color (Red)
        FoodFlowLoadingButton(
            text = "Login",
            onClick = { onEmailLoginClick(email, password) },
            isLoading = authState is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Register Link ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary // Brand Red
                )
            }
        }

        // ── Error / Info States ──
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        if (authState is AuthState.PasswordResetSent) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Password reset email sent!",
                color = MaterialTheme.colorScheme.primary, // Brand Red
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}