package com.grpc.poc.client.controller.domain.usecases.audio

import com.grpc.poc.client.controller.domain.models.AudioChunk

fun interface ListenAudio {
    suspend operator fun invoke(bytesAudio: AudioChunk)
}

internal class ListenAudioImpl(
    private val audioRepository: AudioRepository,
) : ListenAudio {
    fun interface AudioRepository {
        suspend fun listen(bytesAudio: AudioChunk)
    }

    override suspend fun invoke(bytesAudio: AudioChunk) = audioRepository.listen(bytesAudio)
}
