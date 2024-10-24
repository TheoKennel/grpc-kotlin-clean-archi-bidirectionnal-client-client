import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.jetpack.compose) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {

        filter {
            exclude {
                it.file.path.startsWith(
                    project.layout.buildDirectory
                        .get()
                        .dir("generated")
                        .toString(),
                )
            }
        }
        version.set("0.49.1")
        debug.set(true)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)

        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
        }

        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

tasks.create("assemble").dependsOn(":server:installDist")
