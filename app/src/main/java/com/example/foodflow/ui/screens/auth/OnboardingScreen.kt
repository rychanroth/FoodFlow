package com.example.foodflow.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.components.common.FoodFlowLoadingButton
import com.example.foodflow.ui.components.common.FoodFlowTextField
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.AuthState
import com.example.foodflow.ui.state.OnboardingState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.OnboardingViewModel

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
                .verticalScroll(rememberScrollState()) // Prevents breaking when keyboard opens
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Branded Header ──
            // Uses your Theme's headlineLarge (Playfair Display)
            Text(
                text = "Complete Your Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Uses your Theme's bodyMedium (Open Sans)
            Text(
                text = "Help us personalize your experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Avatar Picker ──
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary, // Brand Red border
                        shape = CircleShape
                    )
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

                    // Small edit overlay when an image is selected
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary), // Brand Red
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change photo",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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

            // ── Input Field ──
            FoodFlowTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Your Name",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Primary CTA ──
            FoodFlowLoadingButton(
                text = "Complete Profile",
                onClick = onCompleteClick,
                isLoading = isLoading,
                enabled = name.isNotBlank()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Skip Link ──
            TextButton(
                onClick = onSkipClick,
                enabled = !isLoading
            ) {
                Text(
                    text = "Skip for now",
                    style = MaterialTheme.typography.labelLarge, // Open Sans Bold
                    color = if (isLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else MaterialTheme.colorScheme.primary // Brand Red
                )
            }

            // ── Error Handling ──
            if (onboardingState is OnboardingState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = onboardingState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}