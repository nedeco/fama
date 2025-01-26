package de.osca.fama.smarthomeadapter

import de.osca.fama.settings.BuildConfig

data class MockedBuildConfig(
    override val version: String = "0.0",
    override val supportUrl: String = "https://example.com",
    override val rabbitmqStompUrl: String = "localhost",
    override val rabbitmqStompUsername: String = "testUsername",
    override val rabbitmqStompPassword: String = "testPassword",
    override val sentryDsn: String? = null,
) : BuildConfig
