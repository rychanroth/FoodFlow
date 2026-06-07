package com.example.foodflow.ui.components.restaurant

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.foodflow.ui.state.SubmitPromoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDialog(
    imageUri: Uri?,
    menuItems: List<MenuItem>, // NEW: List of restaurant's items
    state: SubmitPromoState,
    onDismiss: () -> Unit,
    onPickImage: () -> Unit,
    onSubmit: (menuItemId: String, imageUri: Uri) -> Unit // UPDATED signature
) {
    var selectedMenuItemId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val selectedItemName = menuItems.find { it.id == selectedMenuItemId }?.name ?: ""

    AlertDialog(
        onDismissRequest = { if (state !is SubmitPromoState.Loading) onDismiss() },
        title = { Text("Submit Promotional Banner") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Image Preview
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Promo Banner Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Pick / Change Image Button
                OutlinedButton(onClick = onPickImage, modifier = Modifier.fillMaxWidth()) {
                    Text(if (imageUri != null) "Change Banner Image" else "Pick Banner Image")
                }

                // NEW: Menu Item Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItemName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Promoted Menu Item") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = menuItems.isNotEmpty() // Disable if no items
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

                if (menuItems.isEmpty()) {
                    Text("You must have at least one menu item to create a promo.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("Your banner will be reviewed by an admin before going live.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (state is SubmitPromoState.Error) {
                    Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { imageUri?.let { onSubmit(selectedMenuItemId, it) } },
                enabled = imageUri != null && selectedMenuItemId.isNotEmpty() && state !is SubmitPromoState.Loading
            ) {
                if (state is SubmitPromoState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit for Approval")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = state !is SubmitPromoState.Loading) { Text("Cancel") }
        }
    )
}