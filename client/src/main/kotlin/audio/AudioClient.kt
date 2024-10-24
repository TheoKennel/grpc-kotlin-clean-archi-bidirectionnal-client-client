package audio

import common.Config
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.emptyFlow
import java.io.Closeable
import java.util.*
import java.util.concurrent.TimeUnit

class AudioClient(
    private val channel: ManagedChannel,
) : Closeable {
    private val stub = AudioGuideGrpcKt.AudioGuideCoroutineStub(channel)
    private val randomClientId = UUID.randomUUID().toString()

    override fun close() {
        channel.shutdown().awaitTermination(1, TimeUnit.MINUTES)
    }

    suspend fun initializeConnection() {
        println("init connection")
        val request =
            connectedClient {
                this.clientId = randomClientId
            }
        val response = stub.initializeConnection(request)
        println("Connected to server with message: ${response.message}")
    }

    suspend fun clientDisconnection() {
        val request =
            connectedClient {
                this.clientId = randomClientId
            }
        val response = stub.clientDisconnection(request)
        println("Disconnected from server with message: ${response.message}")
    }

    // comment, testing Ktlint

    suspend fun startAudio() {
        println("start audio HELLO")
        try {
            val response = stub.startAudio(emptyFlow())
            var count = 0
            response.collect {
                count++
                println("Received: ${it.command} time : $count")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Audio started error: ${e.message}")
            clientDisconnection()
        }
    }
}

suspend fun main() {
    val port = Config.PORT

    val channel =
        ManagedChannelBuilder
            .forAddress(
                "localhost",
                port,
            ).keepAliveTime(
                30,
                TimeUnit.SECONDS,
            ).keepAliveTimeout(
                15,
                TimeUnit.SECONDS,
            ).keepAliveWithoutCalls(true)
            .idleTimeout(Long.MAX_VALUE, TimeUnit.DAYS)
            .usePlaintext()
            .build()

    AudioClient(channel).use {
        it.initializeConnection()
        it.startAudio()
//          it.startAudioFirst()
//          it.clientDisconnection()
    }
}
