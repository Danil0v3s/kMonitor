package ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mahm.Reader
import ui.SettingsUi
import ui.ServerUi

@Composable
fun App(
    reader: Reader = Reader()
) = MaterialTheme {
    Row(modifier = Modifier.fillMaxSize()) {
        Column {
            ServerUi(reader)
            Divider()
            SettingsUi()
        }
    }
}




