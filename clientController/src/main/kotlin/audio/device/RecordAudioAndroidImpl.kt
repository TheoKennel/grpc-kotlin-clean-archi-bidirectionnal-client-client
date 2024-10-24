package com.grpc.poc.client.controller.audio.device

import com.grpc.poc.client.controller.audio.domain.RecordAudioImpl

internal class RecordAudioAndroidImpl : RecordAudioImpl.StreamAudioAndroid {
    override suspend fun record(): ByteArray {
    }
}
