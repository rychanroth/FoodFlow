package com.example.foodflow.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodflow.data.model.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_preference")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        val FAVORITES_KEY = stringSetPreferencesKey("favorite_item_ids") // NEW V3
    }

    val themeFlow = context.dataStore.data.map { preferences ->
        val themeString = preferences[THEME_KEY] ?: ThemePreference.SYSTEM.name
        ThemePreference.valueOf(themeString)
    }

    suspend fun setThemePreference(preference: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = preference.name
        }
    }

    val dynamicColorFlow = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] ?: true // Default to true
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    // NEW V3: Favorites
    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITES_KEY] ?: emptySet()
    }

    suspend fun toggleFavorite(itemId: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            if (currentFavorites.contains(itemId)) {
                preferences[FAVORITES_KEY] = currentFavorites - itemId
            } else {
                preferences[FAVORITES_KEY] = currentFavorites + itemId
            }
        }
    }
}