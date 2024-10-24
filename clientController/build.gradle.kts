plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":stub"))
    runtimeOnly(libs.grpc.netty)
}

tasks.register<JavaExec>("AudioController") {
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.grpc.poc.client.controller.audio.AudioControllerKt")
    description = "Start Audio Controller client to execute task"
    group = "audio controller client"
}
//
// val audioControllerStartScript =
//    tasks.register<CreateStartScripts>("createAudioControllerStartScripts") {
//        mainClass.set("com.grpc.poc.client.controller.audio.AudioControllerKt")
//        applicationName = "audio-controller"
//        outputDir = tasks.named<CreateStartScripts>("startScripts").get().outputDir
//        classpath = tasks.named<CreateStartScripts>("startScripts").get().classpath
//        description = "Start Audio Controller client to execute task"
//        group = "audio controller client"
//    }

// tasks.named("startScripts") {
//    dependsOn("createAudioControllerStartScripts")
// }
