package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mahm.Reader

class Server

@Composable
fun ServerUi() = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Magenta)
) {
    val reader = Reader()

    Row {
        Text(text = "Test", textAlign = TextAlign.Center, color = Color.Red)
    }

    Button(onClick = {
        startServer(reader)
    }) {
        Text("Start server")
    }
}

private fun startServer(reader: Reader) {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/") {
                call.respond(reader.readData())
            }
        }
    }.start(wait = false)
}
