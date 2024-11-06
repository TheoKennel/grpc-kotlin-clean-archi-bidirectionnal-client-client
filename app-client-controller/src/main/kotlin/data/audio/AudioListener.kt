package com.grpc.poc.client.controller.data.audio

import com.grpc.poc.client.controller.data.audio.Constant.BUFFER_SIZE
import com.grpc.poc.client.controller.data.audio.Constant.SAMPLE_RATE
import com.grpc.poc.client.controller.domain.models.AudioChunk
import kotlinx.coroutines.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

object AudioListener {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var stopJob: Job? = null
    private var line: SourceDataLine? = null

    private fun initializeLine() {
        try {
            val format = AudioFormat(SAMPLE_RATE, 16, 2, true, false)
            val info = DataLine.Info(SourceDataLine::class.java, format)
            line = AudioSystem.getLine(info) as SourceDataLine
            line?.open(format)
            line?.start()
        } catch (e: Exception) {
            println("Failed to initialize audio line: ${e.message}")
            e.printStackTrace()
        }
    }

    fun playAudio(chunk: AudioChunk) {
        if (line == null) {
            println("Audio line not initialized. Initializing now...")
            initializeLine()
        }

        try {
            line?.let {
                processBytes(it, chunk.bytes)
            } ?: println("Failed to play audio: Audio line is null.")
        } catch (e: Exception) {
            println("Error while playing audio: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun processBytes(
        line: SourceDataLine,
        bytes: ByteArray,
    ) {
        var offset = 0
        while (offset < bytes.size) {
            val bytesToWrite = minOf(BUFFER_SIZE, bytes.size - offset)
            line.write(bytes, offset, bytesToWrite)
            offset += bytesToWrite
        }
    }

    fun stopTask() {
        stopJob?.cancel()
        stopJob =
            scope.launch {
                delay(2000)
                line?.drain()
                line?.close()
                line = null
            }
    }
}
