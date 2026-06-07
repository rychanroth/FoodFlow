package com.example.foodflow.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.ui.components.common.FoodFlowLoadingButton
import com.example.foodflow.ui.components.common.FoodFlowPasswordField
import com.example.foodflow.ui.components.common.FoodFlowTextField
import com.example.foodflow.ui.components.customer.AwaitingVerificationCard
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.viewmodel.AuthViewModel


@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) { }
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
        RegisterContent(
            authState = authState,
            localError = localError,
            email = email,
            onEmailChange = { email = it; localError = null },
            password = password,
            onPasswordChange = { password = it; localError = null },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = { confirmPassword = it; localError = null },
            onRegister = {
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

@Composable
fun RegisterContent(
    authState: AuthState,
    localError: String?,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
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
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(8.dp))

        FoodFlowPasswordField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm Password",
            imeAction = ImeAction.Done
        )
        Spacer(modifier = Modifier.height(16.dp))

        FoodFlowLoadingButton(
            text = "Register",
            onClick = onRegister,
            isLoading = authState is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }

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