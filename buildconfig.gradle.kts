tasks.named("compileKotlin") {
    dependsOn("generateBuildConfig")
}

task("generateBuildConfig") {
    fun getEnv(key: String): String = System.getenv(key)?.let { "\"$it\"" } ?: "missingValue(\"Missing env variable $key in build.\")"

    val outputDir = file("build/generated/src/main/kotlin/de/osca/fama/generated")
    doLast {
        outputDir.mkdirs()
        file("${outputDir.path}/BuildConfigImpl.kt").writeText(
            """
            package de.osca.fama.generated
            
            import de.osca.fama.settings.BuildConfig
            
            class BuildConfigImpl: BuildConfig {
                override val version: String = "$version"
                override val supportUrl: String = ${getEnv("SUPPORT_URL")}
                override val rabbitmqStompUrl: String = ${getEnv("RABBIT_MQ_STOMP_URL")}
                override val rabbitmqStompUsername: String = ${getEnv("RABBIT_MQ_STOMP_USERNAME")}
                override val rabbitmqStompPassword: String = ${getEnv("RABBIT_MQ_STOMP_PASSWORD")}
                override val sentryDsn: String = ${getEnv("SENTRY_DSN")}
            }
            
            """.trimIndent(),
        )
    }
}
