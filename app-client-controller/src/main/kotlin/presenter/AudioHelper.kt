package com.grpc.poc.client.controller.presenter

import audio.AudioGuideGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.io.Closeable
import java.util.concurrent.TimeUnit

class AudioHelper : Closeable {
    companion object {
        const val PORT = 8981
    }

    private val channel: ManagedChannel =
        ManagedChannelBuilder
            .forAddress("localhost", PORT)
            .keepAliveTime(30, TimeUnit.SECONDS)
            .keepAliveTimeout(15, TimeUnit.SECONDS)
            .enableRetry()
            .maxRetryAttempts(20)
            .keepAliveWithoutCalls(true)
            .idleTimeout(Long.MAX_VALUE, TimeUnit.MINUTES)
            .usePlaintext()
            .build()
	
    val stub = AudioGuideGrpcKt.AudioGuideCoroutineStub(channel)

    override fun close() {
        channel.shutdownNow()
    }
}
