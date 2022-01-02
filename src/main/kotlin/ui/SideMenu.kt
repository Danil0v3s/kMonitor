package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    data class Loading(
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
        .background(color = Color.LightGray)
) {
    items(menuItems) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .clickable { onItemSelected(it) }
                .padding(8.dp)
        ) {
            Text(
                text = it.title.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
