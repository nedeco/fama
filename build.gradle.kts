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
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.kotlin.ktor.cio)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation("io.ktor:ktor-client-mock:3.0.3")
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit5)
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
    val dependencies =
        configurations
            .runtimeClasspath
            .get()
            .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
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
