package com.example.foodflow.ui.components.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodflow.data.model.MenuItemCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDialog(
    isEditMode: Boolean,
    categories: List<MenuItemCategory>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    isActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    prepTime: String,              // ✅ ADDED
    onPrepTimeChange: (String) -> Unit, // ✅ ADDED
    imageModel: Any?,
    onPickImageClick: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var categorySearchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val filteredCategories = categories.filter { it.name.contains(categorySearchQuery, ignoreCase = true) }
    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: ""
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit Menu Item" else "Add New Menu Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Image Preview & Picker Button
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Overlay button to Pick or Change image
                OutlinedButton(
                    onClick = onPickImageClick,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                ) {
                    Text(if (imageModel != null) "Change Image" else "Pick Image from Gallery")
                }



                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Dish Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (expanded) categorySearchQuery else selectedCategoryName,
                        onValueChange = { categorySearchQuery = it },
                        readOnly = false,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false; categorySearchQuery = "" }
                    ) {
                        filteredCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategorySelected(category.id)
                                    categorySearchQuery = ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = onPriceChange,
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = prepTime,
                    onValueChange = onPrepTimeChange,
                    label = { Text("Est. Prep Time (mins)") }, // ✅ ADDED
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Available", modifier = Modifier.weight(1f))
                    Switch(checked = isActive, onCheckedChange = onActiveChanged)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = name.isNotBlank()
            ) {
                Text(if (isEditMode) "Save" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
