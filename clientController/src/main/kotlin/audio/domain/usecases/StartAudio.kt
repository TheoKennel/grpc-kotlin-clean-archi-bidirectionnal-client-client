package com.grpc.poc.client.controller.audio.domain.usecases

fun interface StartAudio {
    suspend operator fun invoke(): Result<Unit>
}

internal class StartAudioImpl(
    private val audioRepository: AudioRepository,
) : StartAudio {
    fun interface AudioRepository {
        suspend fun start(): Result<Unit>
    }

    override suspend fun invoke(): Result<Unit> = audioRepository.start()
}
