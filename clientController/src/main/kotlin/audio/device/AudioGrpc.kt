package com.grpc.poc.client.controller.audio.device

import audio.AudioGuideGrpcKt
import audio.Command
import audio.audioCommand
import audio.audioStream
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.flow
import java.io.Closeable
import java.util.concurrent.TimeUnit

class AudioGrpc : Closeable {
    private val port = Config.PORT
    private val channel: ManagedChannel =
        ManagedChannelBuilder
            .forAddress("localhost", port)
            .keepAliveTime(30, TimeUnit.SECONDS)
            .keepAliveTimeout(15, TimeUnit.SECONDS)
            .keepAliveWithoutCalls(true)
            .idleTimeout(Long.MAX_VALUE, TimeUnit.MINUTES)
            .usePlaintext()
            .build()

    private val stub = AudioGuideGrpcKt.AudioGuideCoroutineStub(channel)

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    suspend fun startAudio() {
        try {
            val request =
                flow {
                    val audio =
                        audioCommand {
                            command = Command.START
                            clientId = "evasion-app"
                        }
                    emit(audio)
                }

            val response = stub.startAudio(request)

            response.collect {
                println("Command start send")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun stopAudio() {
        val request =
            flow {
                val command =
                    audioCommand {
                        command = Command.STOP
                        clientId = "evasion-app"
                    }
                emit(command)
            }

        stub.stopAudio(request).collect {
            println("Command stop send")
        }
    }

    suspend fun broadcastAudioToClient() {
        val request =
            flow {
                val stream =
                    audioStream {
                        clientId = "evasion-app"
//                        audioData =
                    }
                emit(stream)
            }
    }
}

object Config {
    const val PORT: Int = 8981
}
