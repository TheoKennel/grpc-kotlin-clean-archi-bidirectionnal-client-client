package audio

import common.Config
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

enum class Command {
    START,
    STOP,
}

// coment, ktlint check

class AudioServer(
    private val port: Int,
    private val server: Server =
        ServerBuilder
            .forPort(
                port,
            ).permitKeepAliveWithoutCalls(true)
            .permitKeepAliveTime(15, TimeUnit.SECONDS)
            .maxConnectionIdle(Long.MAX_VALUE, TimeUnit.MINUTES)
            .maxConnectionAge(Long.MAX_VALUE, TimeUnit.MINUTES)
            .addService(AudioService())
            .build(),
) {
    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down")
                this@AudioServer.stop()
            },
        )
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private fun stop() {
        server.shutdown()
    }

    internal class AudioService : AudioGuideGrpcKt.AudioGuideCoroutineImplBase() {
        private val audioFlow = MutableSharedFlow<AudioStream>()

        private val audioCommand = MutableSharedFlow<AudioCommand>()

        private val connectedClients = mutableSetOf<String>()

        private val mutex = Mutex()

        override suspend fun initializeConnection(request: ConnectedClient): GenericResponse {
            mutex.withLock {
                connectedClients.add(request.clientId)
            }
            println("Connected client ${request.clientId}")
            println("Connected client list $connectedClients")
            return genericResponse {
                message = "Connected to audio server"
            }
        }

        override fun broadcastAudioToAll(requests: Flow<AudioStream>): Flow<AudioStream> =
            flow {
                try {
                    requests.collect { request ->
                        connectedClients.forEach { clientId ->
                            if (clientId !== request.clientId) {
                                audioFlow.emit(request)
                            }
                        }
                    }
                } catch (e: StatusException) {
                    println("Error occurred while broadcasting audio : ${e.message}")
                    throw StatusException(
                        Status.INTERNAL
                            .withDescription("Error occurred while broadcasting audio : ${e.message}")
                            .withCause(e),
                    )
                }
            }

        override fun broadcastAudioToClient(requests: Flow<AudioStream>): Flow<AudioStream> =
            flow {
                try {
                    requests.collect { request ->
                        connectedClients.forEach { clientId ->
                            if (clientId === request.targetClientId) {
                                audioFlow.emit(request)
                            }
                        }
                    }
                } catch (e: StatusException) {
                    println("Error occurred while broadcasting audio : ${e.message}")
                    throw StatusException(
                        Status.INTERNAL
                            .withDescription("Error occurred while broadcasting audio : ${e.message}")
                            .withCause(e),
                    )
                }
            }

        override fun startAudio(requests: Flow<AudioCommand>): Flow<AudioCommand> =
            flow {
                try {
                    requests.collect { command ->
                        delay(1000)
                        audioCommand.emit(command)
                    }
                    audioCommand.collect {
                        emit(it)
                    }
                } catch (e: StatusException) {
                    throw StatusException(Status.INTERNAL.withDescription("Error occurred while starting audio : ${e.message}"))
                } catch (e: Exception) {
                    println("There is an exception : $e")
                }
            }

        override fun stopAudio(requests: Flow<AudioCommand>): Flow<AudioCommand> =
            flow {
                try {
                    val audioRequest =
                        audioCommand {
                            command = Command.STOP
                        }
                    audioCommand.emit(audioRequest)
                } catch (e: StatusException) {
                    throw StatusException(Status.INTERNAL.withDescription("Error occurred while stopping audio : ${e.message}"))
                }
            }

        override suspend fun clientDisconnection(request: ConnectedClient): GenericResponse {
            mutex.withLock {
                connectedClients.remove(request.clientId)
            }
            return genericResponse {
                message = "Client remove from client id list in audio server"
            }
        }
    }
}

fun main() {
    val server = AudioServer(Config.PORT)
    server.start()
    server.blockUntilShutdown()
}
