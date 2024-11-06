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
                launch {
                    while (true) {
                        try {
                            commandStream()
                        } catch (e: Exception) {
                            println("Command stream failed: $e. Retrying in 10 seconds...")
                            delay(10000)
                        }
                    }
                }
			
                launch {
                    while (true) {
                        try {
                            audioStream()
                        } catch (e: Exception) {
                            println("Audio stream failed: $e. Retrying in 10 seconds...")
                            delay(10000)
                        }
                    }
                }
            }
        }

        private suspend fun commandStream() {
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
            try {
                println("Start of audio stream")
                helper.stub.broadcastAudio(request).collect { audio ->
                    listenAudio.invoke(
                        AudioChunk(
                            audio.audioData.toByteArray(),
                            audio.sequenceNumber,
                        ),
                    )
                }
            } catch (e: Exception) {
                println("Got a pb in audio Stream audio client : $e")
            }
        }
    }
