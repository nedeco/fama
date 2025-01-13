package de.osca.fama.settings

import de.osca.fama.smarthomeadapter.SmartHomeAdapter

object Settings {
    // General
    val DEBUG by envBoolean("DEBUG")

    val ENABLE_SENTRY by envBoolean("ENABLE_SENTRY")
    val SMART_HOME_TYPE by envEnum<SmartHomeAdapter.Type>("SMART_HOME_TYPE")

    // Devices
    val ENABLE_SENSOR_STATION by envBoolean("ENABLE_SENSOR_STATION", defaultValue = true)

    // MQTT
    val MQTT_HOST: String? by envString("MQTT_HOST")
    val MQTT_PORT: Int by envInt("MQTT_PORT", defaultValue = 1883)
    val MQTT_TLS_ENABLED: Boolean by envBoolean("MQTT_TLS_ENABLED")
    val MQTT_CLIENT_ID: String by envString("MQTT_CLIENT_ID", defaultValue = "fama")
    val MQTT_USERNAME: String? by envString("MQTT_USERNAME")
    val MQTT_PASSWORD: String? by envString("MQTT_PASSWORD")

    // Home Assistant
    val HOME_ASSISTANT_DISCOVERY_PREFIX: String? by envString("HOME_ASSISTANT_DISCOVERY_PREFIX", defaultValue = "homeassistant")
}
