package com.grpc.poc.client.controller.domain.models

data class AudioChunk(
    val bytes: ByteArray,
    val sequenceNumber: Int,
)
