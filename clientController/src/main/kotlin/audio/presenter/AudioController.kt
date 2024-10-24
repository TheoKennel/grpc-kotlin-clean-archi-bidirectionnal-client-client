package com.grpc.poc.client.controller.audio.presenter

import audio.Command
import audio.audioCommand
import kotlinx.coroutines.flow.flow

class AudioController : AudioManager() {
    suspend fun start() {
        val request =
            flow {
                val audio =
                    audioCommand {
                        command = Command.START
                        clientId = CONTROLLER_ID
                    }
                emit(audio)
            }
        helper.stub.startAudio(request).collect {}
    }

    suspend fun stop() {
        val request =
            flow {
                val audio =
                    audioCommand {
                        command = Command.STOP
                        clientId = CONTROLLER_ID
                    }
                emit(audio)
            }
        helper.stub.stopAudio(request).collect {}
    }

    companion object {
        const val CONTROLLER_ID = "evasion-app"
    }
}
