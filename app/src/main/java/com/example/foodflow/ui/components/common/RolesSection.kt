package com.example.foodflow.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
fun RolesSection(
    userRole: UserRole,
    applyState: RoleApplyState,
    onSubmitApplication: (UserRole, String) -> Unit,
    onResetApplyState: () -> Unit
) {
    var showApplicationDialog by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.DRIVER) }

    Column {
        Text("Roles & Applications", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Role: ${userRole.name}", style = MaterialTheme.typography.bodyLarge)

                if (userRole == UserRole.CUSTOMER) {
                    Spacer(Modifier.height(16.dp))
                    Text("Interested in partnering with us?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedRole = UserRole.DRIVER; showApplicationDialog = true },
                            modifier = Modifier.weight(1f)
                        ) { Text("Be a Driver") }
                        OutlinedButton(
                            onClick = { selectedRole = UserRole.RESTAURANT; showApplicationDialog = true },
                            modifier = Modifier.weight(1f)
                        ) { Text("Add Restaurant") }
                    }
                }
            }
        }
    }

    if (showApplicationDialog) {
        RoleApplicationDialog(
            requestedRole = selectedRole,
            applyState = applyState,
            onDismiss = {
                showApplicationDialog = false
                onResetApplyState()
            },
            onSubmit = { role, details -> onSubmitApplication(role, details) }
        )
    }
}