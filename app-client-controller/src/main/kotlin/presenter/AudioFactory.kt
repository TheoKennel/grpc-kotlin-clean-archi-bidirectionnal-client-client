package com.grpc.poc.client.controller.presenter

import javax.inject.Inject

class AudioFactory
    @Inject
    constructor(
        private val audioClient: AudioClient,
        private val audioController: AudioController,
    ) {
        fun createFactory(appType: AppType): AudioManager =
            when (appType) {
                AppType.CLIENT -> audioClient
                AppType.CONTROLLER -> audioController
            }
    }
