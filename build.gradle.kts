
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
    application
}

group = "de.osca.fama"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.krossbow.stomp)
    implementation(libs.bundles.kmqtt)
    implementation(libs.kermit)
    implementation(libs.logback)

    implementation(libs.kotlin.ktor.cio)
}

application {
    mainClass = "de.osca.fama.MainKt"
}

kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir("build/generated/src/main/kotlin")
        }
    }
}

tasks.jar {
    manifest.attributes["Main-Class"] = application.mainClass
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named("compileKotlin") {
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
