package com.grpc.poc.client.controller.di

import com.grpc.poc.client.controller.domain.usecases.audio.RecordAudio
import com.grpc.poc.client.controller.domain.usecases.audio.StopAudio
import com.grpc.poc.client.controller.presenter.AudioClient
import com.grpc.poc.client.controller.presenter.AudioController
import com.grpc.poc.client.controller.presenter.AudioFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AudioPresenterModule {
    @Provides
    @Singleton
    fun provideAudioController(
        stopAudio: StopAudio,
        recordAudio: RecordAudio,
    ): AudioController = AudioController(recordAudio, stopAudio)

    @Provides
    @Singleton
    fun provideAudioFactory(
        audioClient: AudioClient,
        audioController: AudioController,
    ): AudioFactory = AudioFactory(audioClient, audioController)
}
