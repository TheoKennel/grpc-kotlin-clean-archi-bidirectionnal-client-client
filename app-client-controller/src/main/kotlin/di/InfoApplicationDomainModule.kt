package com.grpc.poc.client.controller.di

import com.grpc.poc.client.controller.data.application.ApplicationInfosRepositoryImpl
import com.grpc.poc.client.controller.domain.usecases.application.GetApplicationSize
import com.grpc.poc.client.controller.domain.usecases.application.GetApplicationSizeImpl
import com.grpc.poc.client.controller.domain.usecases.application.GetApplicationVersion
import com.grpc.poc.client.controller.domain.usecases.application.GetApplicationVersionImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class InfoApplicationDomainModule {
    @Provides
    @Singleton
    fun provideGetApplicationSize(repository: ApplicationInfosRepositoryImpl): GetApplicationSize = GetApplicationSizeImpl(repository)

    @Provides
    @Singleton
    fun provideGetApplicationVersion(repository: ApplicationInfosRepositoryImpl): GetApplicationVersion =
        GetApplicationVersionImpl(repository)
}
