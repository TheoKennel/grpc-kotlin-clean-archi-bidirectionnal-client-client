package com.grpc.poc.client.controller.presenter

abstract class AudioManager {
    protected val helper: AudioHelper = AudioHelper()

    fun close() = run { helper.close() }
}
