package com.grpc.poc.client.controller.di

import com.grpc.poc.client.controller.data.application.ApplicationInfosRepositoryImpl
import com.grpc.poc.client.controller.data.audio.AudioRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class RepositoryDataModule {
    @Provides
    @Singleton
    fun provideApplicationInfoRepository(): ApplicationInfosRepositoryImpl = ApplicationInfosRepositoryImpl()

    @Provides
    @Singleton
    fun provideAudioRepository(): AudioRepositoryImpl = AudioRepositoryImpl()
}
