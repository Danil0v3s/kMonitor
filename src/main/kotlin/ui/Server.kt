package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.application.ApplicationStarted
import io.ktor.application.ApplicationStopped
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.EngineConnectorConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import mahm.Reader

data class ServerState(
    val isRunning: Boolean = false,
    val port: Int = 8080
)

sealed class ServerEvent {
    data class Started(val connector: EngineConnectorConfig) : ServerEvent()
    object Stopped : ServerEvent()
}

class Server

@Composable
fun ServerUi(
    reader: Reader
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
) {
    var state by remember { mutableStateOf(ServerState()) }
    var server by remember { mutableStateOf<NettyApplicationEngine?>(null) }
    var serverInfo by remember { mutableStateOf("Server is not running") }

    Text(
        text = "Server Configuration",
        style = TextStyle(
            fontSize = 18.sp
        )
    )

    Text(text = serverInfo)

    Button(onClick = {
        if (!state.isRunning) {
            server = startServer(reader, state.port) {
                state = when (it) {
                    is ServerEvent.Started -> {
                        serverInfo = "Server is running at ${it.connector.host}:${it.connector.port}"
                        state.copy(isRunning = true)
                    }
                    is ServerEvent.Stopped -> {
                        serverInfo = "Server is not running"
                        state.copy(isRunning = false)
                    }
                }
            }
        } else {
            server?.stop(1000L, 3000L)
        }
    }) {
        Text(if (state.isRunning) "Stop server" else "Start server")
    }
}

private fun startServer(
    reader: Reader,
    port: Int,
    onServerEvent: (ServerEvent) -> Unit
): NettyApplicationEngine {
    val server = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/") {
                call.respond(reader.readData())
            }
        }

        environment.monitor.apply {
            subscribe(ApplicationStarted) {
                onServerEvent(ServerEvent.Started((environment as ApplicationEngineEnvironment).connectors.first()))
            }
            subscribe(ApplicationStopped) {
                onServerEvent(ServerEvent.Stopped)
            }
        }
    }.start(wait = false)

    return server
}
