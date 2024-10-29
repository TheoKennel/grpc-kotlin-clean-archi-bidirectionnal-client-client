import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import audio.AudioServer
import com.grpc.poc.client.controller.di.DaggerAppComponent
import com.grpc.poc.client.controller.presenter.AppType
import com.grpc.poc.client.controller.presenter.AudioClient
import com.grpc.poc.client.controller.presenter.AudioController
import kotlinx.coroutines.launch

@Composable
fun client1Window(onClose: () -> Unit) {
    Window(onCloseRequest = onClose, title = "Client 1") {
        client1Screen()
    }
}

@Composable
fun client2Window(onClose: () -> Unit) {
    Window(onCloseRequest = onClose, title = "Client 2") {
        controllerScreen()
    }
}

@Composable
fun serverWindow(onClose: () -> Unit) {
    Window(onCloseRequest = onClose, title = "Serveur") {
        serverScreen()
    }
}

@Composable
fun client1Screen() {
    var log by remember { mutableStateOf("Logs...") }
    var audioState by remember { mutableStateOf("Audio STOP") }
    val audioFactory by lazy {
        DaggerAppComponent.create().getAudioFactory()
    }
    val audioClient = audioFactory.createFactory(AppType.CLIENT) as AudioClient

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Client", color = Color.White)
        Text("État de l'audio : $audioState", color = Color.White)
		
        BasicTextField(
            value = log,
            onValueChange = { log = it },
            modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Gray),
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            audioClient.close()
        }
    }
}

@Composable
fun controllerScreen() {
    var target by remember { mutableStateOf("Aucune cible") }
    var isRecording by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val audioFactory by lazy {
        DaggerAppComponent.create().getAudioFactory()
    }

    val audioController = audioFactory.createFactory(AppType.CONTROLLER) as AudioController

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Fenêtre du Controller", color = Color.White)

        var expanded by remember { mutableStateOf(false) }
        val items = listOf("BLUE", "ALL")

        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
            ) {
                Text(target)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(onClick = {
                        target = item
                        expanded = false
                    }) {
                        Text(item)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    audioController.start(target)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Text("Start")
        }

        Button(
            onClick = {
                scope.launch {
                    audioController.stop(target)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Text("Stop")
        }

        Button(
            onClick = {
                isRecording = true
                scope.launch {
                    audioController.recordAudio(target)
                    isRecording = false
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = if (isRecording) Color.Red else Color.LightGray),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Text(if (isRecording) "Recording" else "Record")
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            audioController.close()
        }
    }
}

@Composable
fun serverScreen() {
    val audioServer = remember { AudioServer(8981) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Fenêtre du Serveur", color = Color.White)
		
        Spacer(modifier = Modifier.height(16.dp))
		
        Button(
            onClick = { audioServer.start() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Start serveur")
        }
		
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { audioServer.start() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Stop serveur")
        }
    }
}

fun main() =
    application {
        serverWindow(onClose = ::exitApplication)
        client1Window(onClose = ::exitApplication)
        client2Window(onClose = ::exitApplication)
    }
