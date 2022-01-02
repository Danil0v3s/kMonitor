package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

typealias Content = @Composable () -> Unit

fun <T : Any> T.asContent(content: @Composable (T) -> Unit): Content = { content(this) }

sealed class MenuItem {
    abstract val content: Content
    abstract val title: String

    data class Configuration(
        override val content: Content,
        override val title: String
    ) : MenuItem()

    data class Server(
        override val content: Content,
        override val title: String
    ) : MenuItem()
}

@Composable
fun SideMenu(
    menuItems: List<MenuItem>,
    onItemSelected: (MenuItem) -> Unit
) = LazyColumn(
    modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(0.33f)
) {
    items(menuItems) {
        Text(
            text = it.title,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemSelected(it) }
        )
    }
}
