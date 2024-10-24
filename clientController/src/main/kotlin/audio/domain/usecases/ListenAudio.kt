package com.grpc.poc.client.controller.audio.domain.usecases

import kotlinx.coroutines.flow.Flow

fun interface ListenAudio {
    suspend operator fun invoke(audio: Flow<ByteArray>): Result<Unit>
}

internal class ListenAudioImpl(
    private val audioRepository: AudioRepository,
) : ListenAudio {
    fun interface AudioRepository {
        suspend fun listen(audio: Flow<ByteArray>): Result<Unit>
    }

    override suspend fun invoke(audio: Flow<ByteArray>): Result<Unit> = audioRepository.listen(audio)
}
