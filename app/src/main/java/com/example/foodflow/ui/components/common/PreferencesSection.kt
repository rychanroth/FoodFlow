package com.example.foodflow.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.foodflow.data.model.ThemePreference

@Composable
fun PreferencesSection(
    themePreference: ThemePreference,
    dynamicColorEnabled: Boolean,
    onThemeChange: (ThemePreference) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit
) {
    Column {
        Text("Preferences", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = MaterialTheme.shapes.large
        ) {
            Column {
                // Theme Selection
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text("Theme", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ThemePreference.entries.forEach { pref ->
                            FilledTonalButton(
                                onClick = { onThemeChange(pref) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (themePreference == pref) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (themePreference == pref) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(pref.name, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                // Dynamic Color
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FormatPaint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text("Dynamic Color", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Switch(checked = dynamicColorEnabled, onCheckedChange = onDynamicColorChange)
                }
            }
        }
    }
}