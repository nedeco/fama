import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

group = "de.osca.fama"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val nativeTarget = when {
        hostOs == "Mac OS X" && arch == "x86_64" -> macosX64("native")
        hostOs == "Mac OS X" && arch == "aarch64" -> macosArm64("native")
        hostOs == "Linux" && (arch == "x86_64" || arch == "amd64") -> linuxX64("native")
        hostOs == "Linux" && arch == "aarch64" -> linuxArm64("native")
        // Other supported targets are listed here: https://ktor.io/docs/native-server.html#targets
        else -> throw GradleException("Host OS ($hostOs/$arch) is not supported in Fama use Java instead.")
    }

    nativeTarget.binaries.executable {
        entryPoint = "de.osca.fama.main"
    }

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass = "de.osca.fama.MainKt"
        }
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/src/main/kotlin")
            dependencies {
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.bundles.krossbow.stomp)
                implementation(libs.bundles.kmqtt)
                implementation(libs.kermit)

                implementation(libs.kotlin.ktor.cio)
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation(libs.kotlin.ktor.darwin)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("ch.qos.logback:logback-core:1.5.16")
                implementation("ch.qos.logback:logback-classic:1.5.16")
            }
        }
    }
}

tasks.named("compileKotlinNative") {
    dependsOn("generateBuildConfig")
}

tasks.named("compileKotlinJvm") {
    dependsOn("generateBuildConfig")
}

task("generateBuildConfig") {
    fun getEnv(key: String): String = System.getenv(key)?.let { "\"$it\"" } ?: "TODO(\"Missing env variable $key in build.\")"

    val outputDir = file("build/generated/src/main/kotlin/de/osca/fama/generated")
    doLast {
        outputDir.mkdirs()
        file("${outputDir.path}/BuildConfig.kt").writeText(
            """
                package de.osca.fama.generated

                object BuildConfig {
                    const val VERSION: String = "${project.version}"
                    val SUPPORT_URL: String = ${getEnv("SUPPORT_URL")}
                    val RABBIT_MQ_STOMP_URL: String = ${getEnv("RABBIT_MQ_STOMP_URL")}
                    val RABBIT_MQ_STOMP_USERNAME: String = ${getEnv("RABBIT_MQ_STOMP_USERNAME")}
                    val RABBIT_MQ_STOMP_PASSWORD: String = ${getEnv("RABBIT_MQ_STOMP_PASSWORD")}
                    val SENTRY_DSN: String = ${getEnv("SENTRY_DSN")}
                }
            """.trimIndent()
        )
    }
}
