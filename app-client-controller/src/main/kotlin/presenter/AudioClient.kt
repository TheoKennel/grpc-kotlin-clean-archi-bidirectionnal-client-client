package com.grpc.poc.client.controller.presenter

import audio.Command
import audio.connectedClient
import com.grpc.poc.client.controller.domain.models.AudioChunk
import com.grpc.poc.client.controller.domain.usecases.audio.ListenAudio
import com.grpc.poc.client.controller.domain.usecases.audio.StartAudio
import com.grpc.poc.client.controller.domain.usecases.audio.StopAudio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioClient
    @Inject
    constructor(
        private val startAudio: StartAudio,
        private val stopAudio: StopAudio,
        private val listenAudio: ListenAudio,
    ) : AudioManager() {
        private val request =
            connectedClient {
                clientId = "BLUE"
            }
	
        private val job = CoroutineScope(Dispatchers.IO)

        init {
            job.launch {
                while (true) {
                    try {
                        println("Attempting to start command and audio streams...")
                        commandStream()
                        audioStream()
                    } catch (e: Exception) {
                        println("Stream failed: $e. Retrying ...")
                        delay(10000)
                    }
                }
            }
        }

        private suspend fun commandStream() {
            println("Start of command stream")
            helper.stub.audioCommandStream(request).collect { command ->
                if (command.command == Command.START) {
                    startAudio.invoke()
                    println("Received Audio Command START")
                } else if (command.command == Command.STOP) {
                    stopAudio.invoke()
                    println("Received Audio Command STOP")
                }
            }
        }

        private suspend fun audioStream() {
            println("Start of audio stream")
            helper.stub.broadcastAudio(request).collect { audio ->
                listenAudio.invoke(
                    AudioChunk(
                        audio.audioData.toByteArray(),
                        audio.sequenceNumber,
                    ),
                )
                println("Received Audio")
            }
        }
    }
