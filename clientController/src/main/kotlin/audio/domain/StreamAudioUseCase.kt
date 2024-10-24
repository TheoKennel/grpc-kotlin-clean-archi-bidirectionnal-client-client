package com.grpc.poc.client.controller.audio.domain

fun interface RecordAudioUseCase {
    suspend operator fun invoke(): ByteArray
}

internal class RecordAudioImpl(
    private val recordAudioAndroid: StreamAudioAndroid,
) : RecordAudioUseCase {
    fun interface StreamAudioAndroid {
        suspend fun record(): ByteArray
    }

    override suspend fun invoke(): ByteArray = recordAudioAndroid.record()
}
