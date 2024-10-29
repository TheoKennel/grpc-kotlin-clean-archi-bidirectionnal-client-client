package com.grpc.poc.client.controller.data.application

import com.google.gson.Gson
import com.grpc.poc.client.controller.data.models.ApplicationInfosEntity
import com.grpc.poc.client.controller.domain.usecases.application.GetApplicationSizeImpl
import com.grpc.poc.client.controller.domain.usecases.application.GetApplicationVersionImpl
import java.io.File

internal class ApplicationInfosRepositoryImpl :
    GetApplicationSizeImpl.ApplicationInfosRepository,
    GetApplicationVersionImpl.ApplicationInfosRepository {
    override suspend fun getApplicationSize(): Float = setupJson().size

    override suspend fun getApplicationVersion(): Int = setupJson().version

    private fun setupJson(): ApplicationInfosEntity {
        val file = File("application-infos.json")
        val jsonString = file.readText()
        return Gson().fromJson(jsonString, ApplicationInfosEntity::class.java)
    }
}
