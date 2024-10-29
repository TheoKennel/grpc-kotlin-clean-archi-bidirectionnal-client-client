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
    private val file = File("app-client-controller/src/main/kotlin/data/audio/audio-10s.wav")
    private val fileStream = FileInputStream(file)
    private var isRecording: Boolean = false

    fun recordAudio(): Flow<AudioChunk> =
        flow {
            var sequenceNumber = 0
            isRecording = true
            val buffer = ByteArray(BUFFER_SIZE)
            while (isRecording) {
                val readBytes = fileStream.read(buffer, 0, buffer.size)
                if (readBytes > 0) {
                    emit(AudioChunk(buffer.copyOf(readBytes), sequenceNumber))
                    sequenceNumber++
                }
                if (readBytes == -1) {
                    isRecording = false
                    break
                }
            }
            fileStream.close()
        }.flowOn(Dispatchers.IO)
}
