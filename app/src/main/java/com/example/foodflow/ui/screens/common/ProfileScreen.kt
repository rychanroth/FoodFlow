package com.example.foodflow.ui.screens.common

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.ThemePreference
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.components.common.PreferencesSection
import com.example.foodflow.ui.components.common.ProfileHeader
import com.example.foodflow.ui.components.common.RolesSection
import com.example.foodflow.ui.components.customer.FavoritesNavigationRow
import com.example.foodflow.ui.navigation.Route
import com.example.foodflow.ui.state.ProfileState
import com.example.foodflow.ui.state.RoleApplyState
import com.example.foodflow.ui.viewmodel.ApplicationViewModel
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.ProfileViewModel
import com.example.foodflow.ui.viewmodel.SettingsViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    applicationViewModel: ApplicationViewModel = viewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val currentUserId = user?.uid ?: return

    LaunchedEffect(currentUserId) { profileViewModel.loadUserProfile(currentUserId) }

    val profileState by profileViewModel.profileState.collectAsState()
    val isUpdating by profileViewModel.isUpdating.collectAsState()

    val themePreference by settingsViewModel.themePreferenceState.collectAsState()
    val dynamicColorEnabled by settingsViewModel.dynamicColorState.collectAsState()

    val applyState by applicationViewModel.applyState.collectAsState()

    ProfileContent(
        profileState = profileState,
        isUpdating = isUpdating,
        currentUserId = currentUserId,
        themePreference = themePreference,
        dynamicColorEnabled = dynamicColorEnabled,
        applyState = applyState,
        onThemeChange = settingsViewModel::setThemePreference,
        onDynamicColorChange = settingsViewModel::setDynamicColor,
        onAvatarChange = { uri -> profileViewModel.uploadAvatar(currentUserId, uri) },
        onUpdateProfile = { name, phone -> profileViewModel.updateUserProfile(currentUserId, name, phone) },
        onSubmitApplication = { role, details ->
            applicationViewModel.submitApplication(currentUserId, user?.email ?: "", role, details)
        },
        onResetApplyState = applicationViewModel::resetState,
        onLogout = {
            authViewModel.logout()
            navController.navigate(Route.AuthGraph.route) { popUpTo(0) }
        },
        onNavigateToFavorites = { navController.navigate(Route.Favorites.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    profileState: ProfileState,
    isUpdating: Boolean,
    currentUserId: String,
    themePreference: ThemePreference,
    dynamicColorEnabled: Boolean,
    applyState: RoleApplyState,
    onThemeChange: (ThemePreference) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onAvatarChange: (Uri) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onSubmitApplication: (UserRole, String) -> Unit,
    onResetApplyState: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") }
            )
        }
    ) { paddingValues ->
        when (profileState) {
            is ProfileState.Loading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileState.Error -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(profileState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ProfileState.Success -> {
                val user = profileState.user

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(Modifier.height(8.dp))

                    // 1. Profile Header & Edit
                    ProfileHeader(
                        user = user,
                        isUpdating = isUpdating,
                        onAvatarChange = onAvatarChange,
                        onUpdateProfile = onUpdateProfile
                    )

                    FavoritesNavigationRow(onClick = onNavigateToFavorites)

                    // 2. App Preferences
                    PreferencesSection(
                        themePreference = themePreference,
                        dynamicColorEnabled = dynamicColorEnabled,
                        onThemeChange = onThemeChange,
                        onDynamicColorChange = onDynamicColorChange
                    )

                    // 3. Role Applications
                    RolesSection(
                        userRole = user.role,
                        applyState = applyState,
                        onSubmitApplication = onSubmitApplication,
                        onResetApplyState = onResetApplyState
                    )

                    // 4. Logout
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Logout")
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
