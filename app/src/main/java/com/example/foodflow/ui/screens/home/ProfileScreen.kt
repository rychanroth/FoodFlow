package com.example.foodflow.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.foodflow.R
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.ui.viewmodel.ProfileState
import com.example.foodflow.ui.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    LaunchedEffect(currentUserId) { viewModel.loadUserProfile(currentUserId) }

    val profileState by viewModel.profileState.collectAsState()

    Scaffold(
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
        when (val state = profileState) {
            is ProfileState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileState.Success -> {
                ProfileContent(
                    modifier = Modifier.padding(paddingValues),
                    user = state.user,
                    onAvatarChange = { viewModel.uploadAvatar(currentUserId, it) }
                )
            }
            is ProfileState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    user: AppUser,
    onAvatarChange: (Uri) -> Unit
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
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = selectedImageUri ?: user.avatarUrl.ifEmpty { R.drawable.ic_launcher_foreground }, // Add a default placeholder
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            FloatingActionButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.size(36.dp)
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
        Text("Saved Addresses", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        if (user.addresses.isEmpty()) {
            Text("No addresses saved.")
        } else {
            user.addresses.forEach { address ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(address.street, fontWeight = FontWeight.Bold)
                            Text(address.city)
                        }
                    }

            }
        }
    }
}