package com.grpc.poc.client.controller.audio.domain.usecases

fun interface StopAudio {
    suspend operator fun invoke(): Result<Unit>
}

internal class StopAudioImpl(
    private val audioRepository: AudioRepository,
) : StopAudio {
    fun interface AudioRepository {
        suspend fun stop(): Result<Unit>
    }

    override suspend fun invoke(): Result<Unit> = audioRepository.stop()
}
