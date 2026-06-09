package com.example.foodflow.ui.components.restaurant

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.ui.components.common.FoodFlowLoadingButton
import com.example.foodflow.ui.state.SubmitPromoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDialog(
    imageUri: Uri?,
    menuItems: List<MenuItem>,
    state: SubmitPromoState,
    onDismiss: () -> Unit,
    onPickImage: () -> Unit,
    onSubmit: (menuItemId: String, imageUri: Uri) -> Unit
) {
    var selectedMenuItemId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val selectedItemName = menuItems.find { it.id == selectedMenuItemId }?.name ?: ""

    AlertDialog(
        onDismissRequest = { if (state !is SubmitPromoState.Loading) onDismiss() },
        title = { Text("Submit Promotional Banner") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Image Picker Section ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = state !is SubmitPromoState.Loading) { onPickImage() },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Promo Banner Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        FilledTonalButton(
                            onClick = onPickImage,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                            enabled = state !is SubmitPromoState.Loading
                        ) {
                            Text("Change Image")
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Pick Banner Image", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // ── Menu Item Selector Dropdown ──
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        if (menuItems.isNotEmpty() && state !is SubmitPromoState.Loading) expanded = !expanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedItemName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Promoted Menu Item") },
                        placeholder = { if (menuItems.isEmpty()) Text("No items available") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        enabled = menuItems.isNotEmpty() && state !is SubmitPromoState.Loading,
                        shape = MaterialTheme.shapes.medium
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        menuItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    selectedMenuItemId = item.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // ── Status / Info Messages ──
                if (menuItems.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Warning, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(4.dp))
                        Text("You must have at least one menu item to create a promo.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                } else if (selectedMenuItemId.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text("Your banner will be reviewed by an admin before going live.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (state is SubmitPromoState.Error) {
                    Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            FoodFlowLoadingButton(
                text = "Submit for Approval",
                onClick = { imageUri?.let { onSubmit(selectedMenuItemId, it) } },
                isLoading = state is SubmitPromoState.Loading,
                enabled = imageUri != null && selectedMenuItemId.isNotEmpty()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = state !is SubmitPromoState.Loading
            ) {
                Text("Cancel")
            }
        }
    )
}