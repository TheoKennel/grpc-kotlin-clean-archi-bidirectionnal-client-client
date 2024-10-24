package com.grpc.poc.client.controller.audio.presenter

import audio.AudioGuideGrpcKt
import com.grpc.poc.client.controller.audio.device.Config
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.io.Closeable
import java.util.concurrent.TimeUnit

class AudioHelper : Closeable {
    private val port = Config.PORT
    private val channel: ManagedChannel =
        ManagedChannelBuilder
            .forAddress("localhost", port)
            .keepAliveTime(30, TimeUnit.SECONDS)
            .keepAliveTimeout(15, TimeUnit.SECONDS)
            .keepAliveWithoutCalls(true)
            .idleTimeout(Long.MAX_VALUE, TimeUnit.MINUTES)
            .usePlaintext()
            .build()
	
    val stub = AudioGuideGrpcKt.AudioGuideCoroutineStub(channel)

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
