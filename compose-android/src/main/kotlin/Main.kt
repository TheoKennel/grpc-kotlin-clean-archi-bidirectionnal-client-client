import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import audio.AudioClient
import audio.AudioServer
import com.grpc.poc.client.controller.audio.device.AudioGrpc
import kotlinx.coroutines.delay
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
        client2Screen()
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
    AudioClient().startAudio()

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Fenêtre du Client 1", color = Color.White)
        Text("État de l'audio : $audioState", color = Color.White)

        // Champ de texte pour afficher les logs
        BasicTextField(
            value = log,
            onValueChange = { log = it },
            modifier =
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Gray),
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { audioState = "Audio START" },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Simuler Audio Start")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { audioState = "Audio STOP" },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Simuler Audio Stop")
        }

        Button(
            onClick = { AudioClient().close() },
        )
    }
}

@Composable
fun client2Screen() {
    var target by remember { mutableStateOf("Aucune cible") }
    var pushToTalkState by remember { mutableStateOf("Relâché") }
    val scope = rememberCoroutineScope()

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Fenêtre du Client 2", color = Color.White)

        var expanded by remember { mutableStateOf(false) }
        val items = listOf("Client 1", "Client 2", "Tous")

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

        // Bouton qui simule le "push to talk"
        Box(
            modifier =
            Modifier
                .size(200.dp, 50.dp)
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            AudioGrpc().startAudio()

                            scope.launch {
                                delay(100)
                                if (pushToTalkState == "Appuyé") {
                                    AudioGrpc().broadcastToAll()
                                }
                            }

                            tryAwaitRelease()

                            AudioGrpc().stopAudio()
                            pushToTalkState = "Relâché"
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(pushToTalkState, color = Color.Black)
        }

        Button(
            onClick = { AudioGrpc().close() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Disconnect")
        }
    }
}

fun onHoldPress() {
    println("Méthode appelée après 0.1s de maintien")
}

fun onReleasePress() {
    println("Méthode appelée lors du relâchement")
}

@Composable
fun serverScreen() {
    AudioServer().start()
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Fenêtre du Serveur", color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Appelle une fonction pour envoyer l'audio */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Envoyer l'audio au client")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour "start"
        Button(
            onClick = { /* Appelle une fonction pour démarrer quelque chose */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Start")
        }

        Button(
            onClick = { AudioServer().blockUntilShutdown() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        ) {
            Text("Disconnect")
        }
    }
}

fun main() =
    application {
        client1Window(onClose = ::exitApplication)
        client2Window(onClose = ::exitApplication)
        serverWindow(onClose = ::exitApplication)
    }
