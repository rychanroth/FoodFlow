package com.example.foodflow.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.UserRole
import com.example.foodflow.ui.state.RoleApplyState

@Composable
fun RoleApplicationDialog(
    requestedRole: UserRole,
    applyState: RoleApplyState,
    onDismiss: () -> Unit,
    onSubmit: (UserRole, String) -> Unit
) {
    var businessDetails by remember { mutableStateOf("") }
    val isLoading = applyState is RoleApplyState.Loading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Apply for ${requestedRole.name}") },
        text = {
            if (applyState is RoleApplyState.Success) {
                Text("Application submitted! Our team will review it shortly.")
            } else {
                Column {
                    Text("Provide details about your ${if (requestedRole == UserRole.DRIVER) "vehicle/experience" else "restaurant/cuisine"}.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = businessDetails,
                        onValueChange = { businessDetails = it },
                        label = { Text("Business Details") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        enabled = !isLoading
                    )
                    if (applyState is RoleApplyState.Error) {
                        Text(applyState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            when (applyState) {
                is RoleApplyState.Success -> TextButton(onClick = onDismiss) { Text("Done") }
                else -> Button(
                    onClick = { onSubmit(requestedRole, businessDetails) },
                    enabled = !isLoading && businessDetails.isNotBlank()
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Submit")
                }
            }
        },
        dismissButton = {
            if (applyState !is RoleApplyState.Success) {
                TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
            }
        }
    )
}