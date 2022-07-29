package ui

import Title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.application.ApplicationStarted
import io.ktor.application.ApplicationStopped
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.http.cio.websocket.send
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.EngineConnectorConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mahm.Reader

data class ServerState(
    val isRunning: Boolean = false,
    val port: String = "8080",
    val user: String = "root",
    val pass: String = "pass",
    val pollingRate: Long = 200L
)

sealed class ServerEvent {
    data class Started(val connector: EngineConnectorConfig) : ServerEvent()
    object Stopped : ServerEvent()
}

class Server

@Composable
fun ServerUi(
    reader: Reader
) {
    var state by remember { mutableStateOf(ServerState()) }
    var server by remember { mutableStateOf<NettyApplicationEngine?>(null) }
    val pollingRateOptions = listOf("200", "300", "400", "500", "600")

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row {
            Column {
                Title(text = "Server configuration")

                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        maxLines = 1,
                        modifier = Modifier.width(120.dp),
                        value = state.port,
                        onValueChange = { state = state.copy(port = it) },
                        label = { Text("Port") }
                    )

                    DropdownMenu(
                        options = pollingRateOptions,
                        onValueChanged = { state = state.copy(pollingRate = it.toLong()) }
                    )
                }

                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        maxLines = 1,
                        modifier = Modifier.width(120.dp),
                        value = state.user,
                        onValueChange = { state = state.copy(user = it) },
                        label = { Text("User") }
                    )

                    OutlinedTextField(
                        maxLines = 1,
                        modifier = Modifier.width(120.dp),
                        value = state.pass,
                        onValueChange = { state = state.copy(pass = it) },
                        label = { Text("Password") }
                    )
                }

                serverButtonRows(
                    state = state,
                    onStopServerClicked = {
                        if (state.isRunning) {
                            server?.stop(1000L, 3000L)
                        }
                    },
                    onStartServerClicked = {
                        if (!state.isRunning) {
                            server = startServer(reader, state) {
                                state = when (it) {
                                    is ServerEvent.Started -> {
                                        state.copy(isRunning = true)
                                    }

                                    is ServerEvent.Stopped -> {
                                        state.copy(isRunning = false)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun serverButtonRows(
    state: ServerState,
    onStopServerClicked: () -> Unit,
    onStartServerClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { onStopServerClicked() }, enabled = state.isRunning) {
            Text("Stop server")
        }

        Button(onClick = { onStartServerClicked() }, enabled = !state.isRunning) {
            Text("Start server")
        }
    }
}

@Composable
fun DropdownMenu(
    options: List<String>,
    onValueChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.first()) }

    Box(
        modifier = Modifier.width(120.dp)
    ) {
        OutlinedTextField(
            maxLines = 1,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterEnd),
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Poll Rate (ms)") }
        )
        Icon(
            modifier = Modifier.align(Alignment.CenterEnd).clickable { expanded = !expanded },
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = ""
        )

        if (expanded) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().background(Color.White)
            ) {
                items(options) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(4.dp).clickable {
                            selectedOption = it
                            onValueChanged(it)
                            expanded = !expanded
                        },
                        text = it
                    )
                }
            }
        }
    }
}

private fun startServer(
    reader: Reader,
    options: ServerState,
    onServerEvent: (ServerEvent) -> Unit
): NettyApplicationEngine {
    val server = embeddedServer(Netty, port = options.port.toInt()) {
        install(WebSockets)
        install(Authentication) {
            basic("auth-basic") {
                validate { credentials ->
                    if (credentials.name == options.user && credentials.password == options.pass) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
        }

        routing {
            authenticate("auth-basic") {
                webSocket("/socket") {
                    reader.currentData.collect {
                        send(Json.encodeToString(it))
                    }
                }
            }
        }

        environment.monitor.apply {
            subscribe(ApplicationStarted) {
                reader.pollingInterval = options.pollingRate.toLong()
                reader.tryOpenMemoryFile()
                onServerEvent(ServerEvent.Started((environment as ApplicationEngineEnvironment).connectors.first()))
            }
            subscribe(ApplicationStopped) {
                onServerEvent(ServerEvent.Stopped)
                reader.stopPolling()
            }
        }
    }.start(wait = false)

    return server
}
