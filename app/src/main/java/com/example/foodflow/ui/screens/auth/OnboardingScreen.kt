package com.example.foodflow.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.ui.state.OnboardingState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.OnboardingViewModel
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState

// ── STATEFUL ──────────────────────────────────────────────
@Composable
fun OnboardingScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onboardingViewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val onboardingState by onboardingViewModel.onboardingState.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedAvatarUri = uri
    }

    LaunchedEffect(onboardingState) {
        if (onboardingState is OnboardingState.Success) {
            val updatedUser = authViewModel.refreshCurrentUser()
            val role = updatedUser?.role
                ?: (authViewModel.authState.value as? AuthState.Success)?.role
                ?: UserRole.CUSTOMER
            val destination = when (role) {
                UserRole.CUSTOMER -> Route.CustomerGraph.route
                UserRole.RESTAURANT -> Route.RestaurantGraph.route
                UserRole.DRIVER -> Route.DriverGraph.route
                UserRole.ADMIN -> Route.AdminGraph.route
            }
            navController.navigate(destination) {
                popUpTo(Route.Onboarding.route) { inclusive = true }
            }
        }
    }

    OnboardingContent(
        name = name,
        onNameChange = { name = it },
        avatarUri = selectedAvatarUri,
        onAvatarClick = { galleryLauncher.launch("image/*") },
        onboardingState = onboardingState,
        onCompleteClick = {
            onboardingViewModel.completeOnboarding(name, selectedAvatarUri, context)
        },
        onSkipClick = {
            onboardingViewModel.skipOnboarding()
        }
    )
}

// ── STATELESS ─────────────────────────────────────────────
@Composable
fun OnboardingContent(
    name: String,
    onNameChange: (String) -> Unit,
    avatarUri: Uri?,
    onAvatarClick: () -> Unit,
    onboardingState: OnboardingState,
    onCompleteClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val isLoading = onboardingState is OnboardingState.Loading

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Complete Your Profile",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Help us personalize your experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar picker
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = !isLoading) { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                if (avatarUri != null) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = "Selected avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add photo",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCompleteClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Complete Profile")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onSkipClick,
                enabled = !isLoading
            ) {
                Text("Skip for now")
            }

            if (onboardingState is OnboardingState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = onboardingState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}