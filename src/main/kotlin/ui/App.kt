package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun App() = MaterialTheme {
    Row(modifier = Modifier.fillMaxSize()) {
        val menuItems = listOf(
            MenuItem.Server(
                content = Server().asContent { ServerUi() },
                title = "Server configuration"
            ),
            MenuItem.Configuration(
                content = Configuration().asContent { ConfigurationUi() },
                title = "App setup"
            ),
        )

        var currentScreen by remember { mutableStateOf(menuItems.first()) }

        SideMenu(menuItems) {
            currentScreen = it
        }

        currentScreen.content()
    }
}
