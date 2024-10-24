package com.grpc.poc.client.controller.audio.domain.usecases

import com.grpc.poc.client.controller.audio.common.TargetAudio
import kotlinx.coroutines.flow.Flow

fun interface RecordAudio {
    operator fun invoke(target: TargetAudio): Flow<ByteArray>
}

internal class RecordAudioImpl(
    private val audioRepository: AudioRepository,
) : RecordAudio {
    fun interface AudioRepository {
        fun record(target: TargetAudio): Flow<ByteArray>
    }

    override fun invoke(target: TargetAudio): Flow<ByteArray> = audioRepository.record(target)
}
