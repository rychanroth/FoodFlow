import com.example.foodflow.data.model.PlatformSettings

sealed interface SettingsState {
    data object Loading : SettingsState
    data class Success(val settings: PlatformSettings) : SettingsState
    data class Error(val message: String) : SettingsState
    data object Saved : SettingsState
}