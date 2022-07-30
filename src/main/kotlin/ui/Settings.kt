package ui

import Label
import Title
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import repository.PreferencesRepository
import win32.WinRegistry

const val PREFERENCE_AUTO_START_SERVER = "PREFERENCE_AUTO_START_SERVER"
const val PREFERENCE_START_MINIMIZED = "PREFERENCE_START_MINIMIZED"

@Composable
fun SettingsUi() = Box(
    modifier = Modifier
        .padding(16.dp)
) {
    Column {
        Title(text = "App settings")
        StartWithWindowsCheckbox()
        StartMinimizedCheckbox()
        AutoStartServerCheckbox()
    }
}

@Composable
private fun StartWithWindowsCheckbox() {
    var state by remember { mutableStateOf(WinRegistry.isAppRegisteredToStartWithWindows()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = { value ->
                state = value
                if (value) {
                    WinRegistry.registerAppToStartWithWindows()
                } else {
                    WinRegistry.removeAppFromStartWithWindows()
                }
            }
        )

        Label(text = "Start with Windows")
    }
}

@Composable
private fun StartMinimizedCheckbox() {
    var state by remember { mutableStateOf(PreferencesRepository.getPreferenceBoolean(PREFERENCE_START_MINIMIZED)) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = { value ->
                state = value
                PreferencesRepository.setPreferenceBoolean(PREFERENCE_START_MINIMIZED, value)
            }
        )

        Label(text = "Start minimized")
    }
}

@Composable
private fun AutoStartServerCheckbox() {
    var state by remember { mutableStateOf(PreferencesRepository.getPreferenceBoolean(PREFERENCE_AUTO_START_SERVER)) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = { value ->
                state = value
                PreferencesRepository.setPreferenceBoolean(PREFERENCE_AUTO_START_SERVER, value)
            }
        )

        Label(text = "Auto start server")
    }
}
