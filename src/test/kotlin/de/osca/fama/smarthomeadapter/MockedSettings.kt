package de.osca.fama.smarthomeadapter

import de.osca.fama.settings.Settings

class MockedSettings(
    override val debug: Boolean = true,
    override val enableSentry: Boolean = false,
    override val smartHomeType: SmartHomeAdapter.Type = SmartHomeAdapter.Type.IB,
    override val enableSensorStation: Boolean = true,
    override val mqttHost: String? = null,
    override val mqttPort: Int = 1883,
    override val mqttTlsEnabled: Boolean = false,
    override val mqttClientId: String = "fama",
    override val mqttUsername: String? = null,
    override val mqttPassword: String? = null,
    override val homeAssistantDiscoveryPrefix: String = "homeassistant",
    override val ioBrokerUrl: String = "http://localhost:8087/v1/",
    override val ioBrokerPrefix: String = "fama",
    override val ioBrokerStationFolderPrefix: String = "sensor-station",
    override val VERSION: String = "1.0-SNAPSHOT",
    override val SUPPORT_URL: String = "https://example.com",
    override val SENTRY_DSN: String = "https://sentry.io",
    override val RABBIT_MQ_STOMP_URL: String = "",
    override val RABBIT_MQ_STOMP_USERNAME: String = "",
    override val RABBIT_MQ_STOMP_PASSWORD: String = "",
) : Settings
