package com.grpc.poc.client.controller.data.audio

import com.grpc.poc.client.controller.data.audio.Constant.BUFFER_SIZE
import com.grpc.poc.client.controller.data.audio.Constant.SAMPLE_RATE
import com.grpc.poc.client.controller.domain.models.AudioChunk
import kotlinx.coroutines.*
import java.io.File
import javax.sound.sampled.*

object AudioListener {
    private const val DELAY = 3000L

    private val scope = CoroutineScope(Dispatchers.IO)
    private var stopJob: Job? = null
    private var line: SourceDataLine? = null
//    private var lastSequenceNumber = -1

    fun getAudioFileFormat(filePath: String) {
        val file = File(filePath)
        try {
            val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(file)
            val format = audioInputStream.format

            println("Sample Rate: ${format.sampleRate}")
            println("Sample Size in Bits: ${format.sampleSizeInBits}")
            println("Channels: ${format.channels}")
            println("Is Big Endian: ${format.isBigEndian}")

            audioInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        try {
//            getAudioFileFormat("app-client-controller/src/main/kotlin/data/audio/audio-10s.wav")
            val format = AudioFormat(SAMPLE_RATE, 16, 1, true, true)
            val info = DataLine.Info(SourceDataLine::class.java, format)
            line = AudioSystem.getLine(info) as SourceDataLine
            line?.open(format)
            line?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playAudio(chunk: AudioChunk) {
        try {
//            TODO( BUG HERE)
//            if (chunk.sequenceNumber != lastSequenceNumber + 1) {
//                println("Erreur de séquence : attendu ${lastSequenceNumber + 1}, reçu ${chunk.sequenceNumber}")
//                return
//            }
//            lastSequenceNumber = chunk.sequenceNumber

            processBytes(line!!, chunk.bytes)
        } catch (e: Exception) {
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
                delay(DELAY)
                line?.drain()
                line?.close()
            }
    }
}
