package com.example.foodflow.ui.components.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodflow.data.model.AppUser

@Composable
fun ProfileHeader(
    user: AppUser,
    isUpdating: Boolean,
    onAvatarChange: (Uri) -> Unit,
    onUpdateProfile: (String, String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            onAvatarChange(it)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                val imageModel = selectedImageUri ?: user.avatarUrl.takeIf { it.isNotEmpty() }
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (!isUpdating) {
                    FilledTonalIconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Change avatar", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.name.ifBlank { "No Name Set" }, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(user.phone.ifBlank { "No phone set" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = { showEditDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }
    }

    if (showEditDialog) {
        ProfileEditDialog(
            initialName = user.name,
            initialPhone = user.phone,
            onDismiss = { showEditDialog = false },
            onSave = { name, phone ->
                onUpdateProfile(name, phone)
                showEditDialog = false
            }
        )
    }
}