package com.grpc.poc.client.controller.di

import com.grpc.poc.client.controller.data.audio.AudioRepositoryImpl
import com.grpc.poc.client.controller.domain.usecases.audio.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AudioDomainModule {
    @Provides
    @Singleton
    fun provideStartAudio(repository: AudioRepositoryImpl): StartAudio = StartAudioImpl(repository)

    @Provides
    @Singleton
    fun provideStopAudio(repository: AudioRepositoryImpl): StopAudio = StopAudioImpl(repository)

    @Provides
    @Singleton
    fun provideRecordAudio(repository: AudioRepositoryImpl): RecordAudio = RecordAudioImpl(repository)

    @Provides
    @Singleton
    fun provideListenAudio(repository: AudioRepositoryImpl): ListenAudio = ListenAudioImpl(repository)
}
