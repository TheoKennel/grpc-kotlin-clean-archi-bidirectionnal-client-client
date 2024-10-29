package com.grpc.poc.client.controller.domain.usecases.application

fun interface GetApplicationVersion {
    suspend operator fun invoke(): Int
}

internal class GetApplicationVersionImpl(
    private val applicationInfoRepository: ApplicationInfosRepository,
) : GetApplicationVersion {
    fun interface ApplicationInfosRepository {
        suspend fun getApplicationVersion(): Int
    }

    override suspend fun invoke(): Int = applicationInfoRepository.getApplicationVersion()
}
