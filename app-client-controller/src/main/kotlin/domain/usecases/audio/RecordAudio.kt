package com.grpc.poc.client.controller.domain.usecases.audio

import com.grpc.poc.client.controller.domain.models.AudioChunk
import kotlinx.coroutines.flow.Flow

fun interface RecordAudio {
    operator fun invoke(): Flow<AudioChunk>
}

internal class RecordAudioImpl(
    private val audioRepository: AudioRepository,
) : RecordAudio {
    fun interface AudioRepository {
        fun record(): Flow<AudioChunk>
    }

    override fun invoke(): Flow<AudioChunk> = audioRepository.record()
}
