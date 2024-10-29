package com.grpc.poc.client.controller.domain.usecases.audio

fun interface StopAudio {
    suspend operator fun invoke()
}

internal class StopAudioImpl(
    private val audioRepository: AudioRepository,
) : StopAudio {
    fun interface AudioRepository {
        suspend fun stop()
    }

    override suspend fun invoke() = audioRepository.stop()
}
