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

class Settings

@Composable
fun SettingsUi() = Box(
    modifier = Modifier
        .padding(16.dp)
) {
    var state by remember { mutableStateOf(false) }

    Column {
        Title(text = "App settings")
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = state,
                onCheckedChange = { value -> state = value}
            )

            Label(text = "Start with the system")
        }
    }
}
