package com.grpc.poc.client.controller

import com.grpc.poc.client.controller.data.audio.AudioRepositoryImpl
import com.grpc.poc.client.controller.di.DaggerAppComponent

suspend fun main() {
    println("Hello World!")
    DaggerAppComponent.create()

    AudioRepositoryImpl().start()
}
