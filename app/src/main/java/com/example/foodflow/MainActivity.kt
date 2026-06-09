package com.example.foodflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodflow.ui.navigation.AppNavigation
import com.example.foodflow.ui.theme.FoodFlowTheme
import com.example.foodflow.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val themePreference by settingsViewModel.themePreferenceState.collectAsState()
            val dynamicColor by settingsViewModel.dynamicColorState.collectAsState()

            FoodFlowTheme(
                themePreference = themePreference,
                dynamicColor = dynamicColor
            ) {
                // AppNavigation handles its own Scaffold and internal screen padding
                AppNavigation()
            }
        }
    }
}
