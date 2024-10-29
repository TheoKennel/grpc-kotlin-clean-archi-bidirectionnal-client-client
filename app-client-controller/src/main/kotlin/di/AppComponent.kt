package com.grpc.poc.client.controller.di

import com.grpc.poc.client.controller.presenter.AudioFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        InfoApplicationDomainModule::class,
        AudioDomainModule::class,
        RepositoryDataModule::class,
    ],
)
interface AppComponent {
    fun getAudioFactory(): AudioFactory
}
