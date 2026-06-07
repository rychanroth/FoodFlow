package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.navigation.Route
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Enter your email address and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Only show the form if the email hasn't been sent yet
        if (authState !is AuthState.PasswordResetSent) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange, // Bubbles the text change up
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSendResetClick, // Bubbles the click event up
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send Reset Link")
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            // SUCCESS STATE
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Check your inbox!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLoginClick) {
            Text("Back to Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordContentPreview() {
    ForgotPasswordContent(AuthState.Idle, "", { t -> }, {}, {})
}
