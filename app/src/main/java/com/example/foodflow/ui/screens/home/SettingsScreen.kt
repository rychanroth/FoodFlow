package com.example.foodflow.ui.screens.home

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodflow.data.model.ThemePreference
import com.example.foodflow.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
) {
    val themePreference by settingsViewModel.themePreferenceState.collectAsState()
    val dynamicColor by settingsViewModel.dynamicColorState.collectAsState()

    SettingsContent(
        themePreference = themePreference,
        dynamicColor = dynamicColor,
        onThemeChange = { settingsViewModel.setThemePreference(it) },
        onDynamicColorChange = { settingsViewModel.setDynamicColor(it) },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    themePreference: ThemePreference,
    dynamicColor: Boolean,
    onThemeChange: (ThemePreference) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Appearance Section
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Color Toggle (Only visible on Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dynamic Color")
                    Switch(
                        checked = dynamicColor,
                        onCheckedChange = onDynamicColorChange
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Theme Preference
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val themes = listOf(ThemePreference.LIGHT, ThemePreference.DARK, ThemePreference.SYSTEM)
            themes.forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (themePreference == theme),
                            onClick = { onThemeChange(theme) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (themePreference == theme),
                        onClick = null // Handled by Row
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(theme.name)
                }
            }
        }
    }
}