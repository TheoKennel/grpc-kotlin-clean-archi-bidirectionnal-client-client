package com.grpc.poc.client.controller.data.audio

import com.grpc.poc.client.controller.domain.models.AudioChunk
import com.grpc.poc.client.controller.domain.usecases.audio.ListenAudioImpl
import com.grpc.poc.client.controller.domain.usecases.audio.RecordAudioImpl
import com.grpc.poc.client.controller.domain.usecases.audio.StartAudioImpl
import com.grpc.poc.client.controller.domain.usecases.audio.StopAudioImpl
import kotlinx.coroutines.flow.Flow

internal class AudioRepositoryImpl :
    ListenAudioImpl.AudioRepository,
    RecordAudioImpl.AudioRepository,
    StartAudioImpl.AudioRepository,
    StopAudioImpl.AudioRepository {
    override suspend fun listen(bytesAudio: AudioChunk) = AudioListener.playAudio(bytesAudio)

    override fun record(): Flow<AudioChunk> = AudioRecorder.recordAudio()

    override suspend fun stop() {
        println("Set up volume to basic before ptt started")
        AudioListener.stopTask()
    }

    override suspend fun start() {
        println("Reduce application volume for ptt")
    }
}
