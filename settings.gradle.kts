rootProject.name = "grpc-poc"

if (startParameter.taskRequests.find { it.args.contains("assemble") } == null) {
    include("proto", "stub", "server", "app-client-controller", "compose-android")
} else {
    include("proto", "stub", "server")
}

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
include(":features")
include("untitled")
include("features")
