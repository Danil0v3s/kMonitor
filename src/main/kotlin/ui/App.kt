package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import mahm.Reader

@Composable
fun App(
    viewModel: AppViewModel = AppViewModel()
) = MaterialTheme {
    Row(modifier = Modifier.fillMaxSize()) {
        val state = viewModel.state.collectAsState(AppState.EMPTY)

        with(state.value) {
            SideMenu(menuItems, viewModel::changeScreen)

            // render current screen
            currentScreen.content()
        }
    }
}

class AppViewModel {

    private val reader = Reader(Dispatchers.IO)

    private val menuItems = listOf(
        MenuItem.Server(
            content = Server().asContent { ServerUi(reader) },
            title = "Server configuration"
        ),
        MenuItem.Configuration(
            content = Configuration().asContent { ConfigurationUi() },
            title = "App setup"
        )
    )

    private val _state = MutableStateFlow(
        AppState(
            menuItems = menuItems,
            currentScreen = menuItems.first()
        )
    )
    val state: Flow<AppState>
        get() = _state

    fun changeScreen(menuItem: MenuItem) {
        _state.value = _state.value.copy(currentScreen = menuItem)
    }
}

data class AppState(
    val menuItems: List<MenuItem>,
    val currentScreen: MenuItem
) {
    companion object {
        val EMPTY: AppState = AppState(
            menuItems = emptyList(),
            currentScreen = MenuItem.Loading(content = {}, title = "Loading")
        )
    }
}
