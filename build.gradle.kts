import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sentry)
    alias(libs.plugins.cyclonedx.bom)
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

configure<KtlintExtension> {
    outputToConsole.set(true)
    outputColorName.set("RED")
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
}

sentry {
    includeSourceContext = true

    org = "open-smart-city"
    projectName = "fama"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
    autoInstallation {
        enabled = true
    }
}

apply("buildconfig.gradle.kts")
