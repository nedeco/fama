package de.osca.fama.settings

class BuildConfigValueMissingException(
    key: String,
) : IllegalArgumentException(
        """
        Variable $key is missing.
        And is not set at compile time.
        """.trimIndent(),
    )

@Throws(BuildConfigValueMissingException::class)
fun missingValue(key: String): Nothing = throw BuildConfigValueMissingException(key)

interface BuildConfig {
    val version: String
    val supportUrl: String
    val rabbitmqStompUrl: String
    val rabbitmqStompUsername: String
    val rabbitmqStompPassword: String
    val sentryDsn: String?
}
