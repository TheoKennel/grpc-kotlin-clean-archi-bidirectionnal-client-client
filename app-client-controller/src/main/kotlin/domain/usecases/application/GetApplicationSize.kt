package com.grpc.poc.client.controller.domain.usecases.application

fun interface GetApplicationSize {
    suspend operator fun invoke(): Float
}

internal class GetApplicationSizeImpl(
    private val applicationInfoRepository: ApplicationInfosRepository,
) : GetApplicationSize {
    fun interface ApplicationInfosRepository {
        suspend fun getApplicationSize(): Float
    }

    override suspend fun invoke(): Float = applicationInfoRepository.getApplicationSize()
}
