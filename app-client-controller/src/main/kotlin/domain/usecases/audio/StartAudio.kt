package com.grpc.poc.client.controller.domain.usecases.audio

fun interface StartAudio {
    suspend operator fun invoke()
}

internal class StartAudioImpl(
    private val audioRepository: AudioRepository,
) : StartAudio {
    fun interface AudioRepository {
        suspend fun start()
    }

    override suspend fun invoke() = audioRepository.start()
}
