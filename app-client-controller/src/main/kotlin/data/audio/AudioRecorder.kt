package com.grpc.poc.client.controller.data.audio

import com.grpc.poc.client.controller.data.audio.Constant.BUFFER_SIZE
import com.grpc.poc.client.controller.domain.models.AudioChunk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream

object AudioRecorder {
    private val file = File("app-client-controller/src/main/kotlin/data/audio/audio-10s-16bit.wav")
    private var isRecording: Boolean = false

    fun recordAudio(): Flow<AudioChunk> =
        flow {
            var sequenceNumber = 0
            isRecording = true
            FileInputStream(file).use { fileStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var readBytes: Int
                while (isRecording) {
                    readBytes = fileStream.read(buffer)
                    if (readBytes > 0) {
                        emit(AudioChunk(buffer.copyOf(readBytes), sequenceNumber))
                        sequenceNumber++
                    } else {
                        isRecording = false
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
}
