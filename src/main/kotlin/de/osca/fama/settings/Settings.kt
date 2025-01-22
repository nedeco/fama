package de.osca.fama.settings

import de.osca.fama.smarthomeadapter.SmartHomeAdapter

interface Settings {
    // General
    val DEBUG: Boolean

    val ENABLE_SENTRY: Boolean
    val SMART_HOME_TYPE: SmartHomeAdapter.Type

    // Devices
    val ENABLE_SENSOR_STATION: Boolean

    // MQTT
    val MQTT_HOST: String?
    val MQTT_PORT: Int
    val MQTT_TLS_ENABLED: Boolean
    val MQTT_CLIENT_ID: String
    val MQTT_USERNAME: String?
    val MQTT_PASSWORD: String?
    val MQTT_ENABLE: Boolean

    // Home Assistant
    val HOME_ASSISTANT_DISCOVERY_PREFIX: String

    // IoBroker
    val IO_BROKER_URL: String
    val IO_BROKER_PREFIX: String
    val IO_BROKER_STATION_FOLDER_PREFIX: String

    // Build Config
    val VERSION: String
    val SUPPORT_URL: String
    val SENTRY_DSN: String
    val RABBIT_MQ_STOMP_URL: String
    val RABBIT_MQ_STOMP_USERNAME: String
    val RABBIT_MQ_STOMP_PASSWORD: String
}
