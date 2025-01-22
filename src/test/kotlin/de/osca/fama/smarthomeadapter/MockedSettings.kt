package de.osca.fama.smarthomeadapter

import de.osca.fama.settings.Settings

class MockedSettings(
    override val DEBUG: Boolean = true,
    override val ENABLE_SENTRY: Boolean = false,
    override val SMART_HOME_TYPE: SmartHomeAdapter.Type = SmartHomeAdapter.Type.IB,
    override val ENABLE_SENSOR_STATION: Boolean = true,
    override val MQTT_HOST: String? = null,
    override val MQTT_PORT: Int = 1883,
    override val MQTT_TLS_ENABLED: Boolean = false,
    override val MQTT_CLIENT_ID: String = "fama",
    override val MQTT_USERNAME: String? = null,
    override val MQTT_PASSWORD: String? = null,
    override val MQTT_ENABLE: Boolean = false,
    override val HOME_ASSISTANT_DISCOVERY_PREFIX: String = "homeassistant",
    override val IO_BROKER_URL: String = "http://localhost:8087/v1/",
    override val IO_BROKER_PREFIX: String = "fama",
    override val IO_BROKER_STATION_FOLDER_PREFIX: String = "sensor-station",
    override val VERSION: String = "1.0-SNAPSHOT",
    override val SUPPORT_URL: String = "https://example.com",
    override val SENTRY_DSN: String = "https://sentry.io",
    override val RABBIT_MQ_STOMP_URL: String = "",
    override val RABBIT_MQ_STOMP_USERNAME: String = "",
    override val RABBIT_MQ_STOMP_PASSWORD: String = ""
) : Settings
