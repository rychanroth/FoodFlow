package com.example.foodflow.ui.screens.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodflow.R
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.ui.state.ProfileState
import com.example.foodflow.ui.viewmodel.AuthViewModel
import com.example.foodflow.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ProfileViewModel = viewModel(),
) {
    val user by authViewModel.currentUser.collectAsState()
    val currentUserId = user?.uid ?: ""

    // Safety check: If user is somehow null, boot them back
    if (currentUserId == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    LaunchedEffect(currentUserId) { viewModel.loadUserProfile(currentUserId) }

    val profileState by viewModel.profileState.collectAsState()

    ProfileLayout(
        profileState = profileState,
        onBackClick = { navController.popBackStack() },
        onAvatarChange = { uri -> viewModel.uploadAvatar(currentUserId, uri) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileLayout(
    profileState: ProfileState,
    onBackClick: () -> Unit,
    onAvatarChange: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (profileState) {
                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        user = profileState.user,
                        onAvatarChange = onAvatarChange
                    )
                }
                is ProfileState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = profileState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: AppUser,
    onAvatarChange: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            onAvatarChange(it)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Section
        Box(contentAlignment = Alignment.BottomEnd) {
            val imageModel = selectedImageUri
                ?: user.avatarUrl.takeIf { it.isNotEmpty() }
                ?: R.drawable.ic_launcher_foreground

            AsyncImage(
                model = imageModel,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            FloatingActionButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.size(36.dp),
                shape = CircleShape
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Info
        Text(user.email, style = MaterialTheme.typography.titleLarge)
        Text("Role: ${user.role.name}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // Addresses (Read-only for MVP)
        Text(
            text = "Saved Addresses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (user.addresses.isEmpty()) {
            Text(
                text = "No addresses saved.",
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            user.addresses.forEach { address ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(address.street, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(address.city, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}