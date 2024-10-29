package audio

import com.google.protobuf.Empty
import common.Config
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class AudioServer(
    private val port: Int,
) {
    private val server: Server =
        ServerBuilder
            .forPort(
                port,
            ).permitKeepAliveWithoutCalls(true)
            .permitKeepAliveTime(15, TimeUnit.SECONDS)
            .maxConnectionIdle(Long.MAX_VALUE, TimeUnit.MINUTES)
            .maxConnectionAge(Long.MAX_VALUE, TimeUnit.MINUTES)
            .addService(AudioService())
            .build()

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
        private val audioFlow = ConcurrentHashMap<String, MutableSharedFlow<AudioResponse>>()
        private val commandFlow = ConcurrentHashMap<String, MutableSharedFlow<AudioCommandResponse>>()

        override suspend fun sendAudioCommand(request: AudioCommandRequest): Empty {
            println("Received request ${request.command}, request client : ${request.clientId}")
            val senderId = request.clientId
            val targetClientId = request.targetClientId

            val response =
                audioCommandResponse {
                    command = request.command
                }
			
            emitToClients(commandFlow, senderId, targetClientId, response)
            return Empty.getDefaultInstance()
        }

        override fun audioCommandStream(request: ConnectedClient): Flow<AudioCommandResponse> =
            channelFlow {
                println("Received request in audio Stream request client : ${request.clientId}")
                val clientId = request.clientId
                val clientFlow = commandFlow.getOrPut(clientId) { MutableSharedFlow() }
			
                val responseJob =
                    launch {
                        clientFlow.collect { response ->
                            send(response)
                        }
                    }
                awaitClose {
                    responseJob.cancel()
                    commandFlow.remove(clientId)
                    println("Client $clientId disconnected from command flow")
                }
            }

        override suspend fun sendAudioData(request: AudioRequest): Empty {
            val senderId = request.clientId
            val targetClientId = request.targetClientId
			
            val response =
                audioResponse {
                    audioData = request.audioData
                    sequenceNumber = request.sequenceNumber
                }
			
            emitToClients(audioFlow, senderId, targetClientId, response)
            return Empty.getDefaultInstance()
        }

        override fun broadcastAudio(request: ConnectedClient): Flow<AudioResponse> =
            channelFlow {
                println("Received request in broadcastAudio Stream request client : ${request.clientId}")
                val clientId = request.clientId
                val clientFlow = audioFlow.getOrPut(clientId) { MutableSharedFlow() }

                val response =
                    launch {
                        clientFlow.collect { response ->
                            send(response)
                        }
                    }

                awaitClose {
                    audioFlow.remove(clientId)
                    response.cancel()
                    println("Client $clientId disconnected from audio flow")
                }
            }

        private suspend fun <T> emitToClients(
            flows: ConcurrentHashMap<String, MutableSharedFlow<T>>,
            senderId: String,
            targetId: String?,
            response: T,
        ) {
            if (targetId != null && targetId != senderId) {
                flows[targetId]?.emit(response)
            } else {
                flows.forEach { (id, flow) ->
                    if (id != senderId) {
                        flow.emit(response)
                    }
                }
            }
        }
    }
}

fun main() {
    val server = AudioServer(Config.PORT)
    server.start()
    server.blockUntilShutdown()
}
