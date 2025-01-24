tasks.named("compileKotlin") {
    dependsOn("generateBuildConfig")
}

task("generateBuildConfig") {
    fun getEnv(key: String): String = System.getenv(key)?.let { "\"$it\"" } ?: "missingValue(\"Missing env variable $key in build.\")"

    val outputDir = file("build/generated/src/main/kotlin/de/osca/fama/generated")
    doLast {
        outputDir.mkdirs()
        file("${outputDir.path}/BuildConfig.kt").writeText(
            """
            package de.osca.fama.generated
            
            class BuildConfigValueMissingException(
                key: String,
            ) : IllegalArgumentException(
                    ""${"\""}
                    Variable ${"$"}key is missing.
                    And is not set at compile time.
                    ""${"\""}.trimIndent(),
            )

            object BuildConfig {
                private fun missingValue(key: String): Nothing = throw BuildConfigValueMissingException(key)

                const val VERSION: String = "$version"
                val SUPPORT_URL: String = ${getEnv("SUPPORT_URL")}
                val RABBIT_MQ_STOMP_URL: String = ${getEnv("RABBIT_MQ_STOMP_URL")}
                val RABBIT_MQ_STOMP_USERNAME: String = ${getEnv("RABBIT_MQ_STOMP_USERNAME")}
                val RABBIT_MQ_STOMP_PASSWORD: String = ${getEnv("RABBIT_MQ_STOMP_PASSWORD")}
                val SENTRY_DSN: String = ${getEnv("SENTRY_DSN")}
            }
            
            """.trimIndent(),
        )
    }
}
