package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

class Server

@Composable
fun ServerUi() = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Magenta)
) {
    Row {
        Text(text = "Test", textAlign = TextAlign.Center, color = Color.Red)
    }
}
