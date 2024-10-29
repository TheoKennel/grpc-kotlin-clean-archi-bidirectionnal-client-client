package com.grpc.poc.client.controller.presenter

import audio.*
import com.google.protobuf.kotlin.toByteString
import com.grpc.poc.client.controller.domain.usecases.audio.RecordAudio
import com.grpc.poc.client.controller.domain.usecases.audio.StopAudio
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioController
    @Inject
    constructor(
        private val recordAudio: RecordAudio,
        private val stopAudio: StopAudio,
    ) : AudioManager() {
        suspend fun start(targetClient: String?) {
            val request = commandRequest(Command.START, targetClient)
            helper.stub.sendAudioCommand(request)
        }

        suspend fun stop(targetClient: String?) {
            stopAudio.invoke()
            val request = commandRequest(Command.STOP, targetClient)
            helper.stub.sendAudioCommand(request)
        }

        suspend fun recordAudio(targetClient: String?) {
            recordAudio.invoke().collect { data ->
                val request =
                    audioRequest {
                        clientId = CONTROLLER_ID
                        audioData = data.bytes.toByteString()
                        if (targetClient != null) {
                            targetClientId = targetClient
                        }
                        sequenceNumber = data.sequenceNumber
                    }
                helper.stub.sendAudioData(request)
            }
        }

        private fun commandRequest(
            command: Command,
            targetClient: String?,
        ): AudioCommandRequest =
            audioCommandRequest {
                clientId = CONTROLLER_ID
                this.command = command
                if (targetClient != null) {
                    targetClientId = targetClient
                }
            }

        companion object {
            const val CONTROLLER_ID = "evasion-app"
        }

        private fun stopAudio() {
            helper.close()
        }
    }
