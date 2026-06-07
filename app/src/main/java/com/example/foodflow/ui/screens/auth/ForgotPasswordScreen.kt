package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

        if (authState !is AuthState.PasswordResetSent) {
            FoodFlowTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(16.dp))

            FoodFlowLoadingButton(
                text = "Send Reset Link",
                onClick = onSendResetClick,
                isLoading = authState is AuthState.Loading
            )

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
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