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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.foodflow.ui.components.common.FoodFlowLoadingButton
import com.example.foodflow.ui.components.common.FoodFlowTextField
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.viewmodel.AuthViewModel


@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    ForgotPasswordContent(
        authState = authState,
        email = email,
        onEmailChange = { email = it },
        onSendResetClick = { authViewModel.sendPasswordReset(email) },
        onBackToLoginClick = {
            authViewModel.resetState()
            navController.popBackStack(Route.Login.route, inclusive = false)
        }
    )
}

@Composable
fun ForgotPasswordContent(
    authState: AuthState,
    email: String,
    onEmailChange: (String) -> Unit,
    onSendResetClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Prevents breaking when keyboard opens
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Branded Header ──
        // Uses your Theme's headlineLarge (Playfair Display)
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Uses your Theme's bodyMedium (Open Sans)
        Text(
            text = "Enter your email address and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (authState !is AuthState.PasswordResetSent) {
            // ── Input & Action ──
            FoodFlowTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email Address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(24.dp))

            FoodFlowLoadingButton(
                text = "Send Reset Link",
                onClick = onSendResetClick,
                isLoading = authState is AuthState.Loading
            )

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // ── Success State ──
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary // Brand Red
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Uses your Theme's headlineMedium (Playfair Display)
            Text(
                text = "Check your inbox!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Uses your Theme's bodyMedium (Open Sans)
            Text(
                text = "Follow the instructions in the email to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Back to Login Link ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Remember your password?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onBackToLoginClick, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary // Brand Red
                )
            }
        }
    }
}