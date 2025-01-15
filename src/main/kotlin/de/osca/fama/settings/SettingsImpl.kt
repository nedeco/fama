package de.osca.fama.settings

import de.osca.fama.smarthomeadapter.SmartHomeAdapter

class SettingsImpl: Settings {
    override val DEBUG by envBoolean("DEBUG")
    override val ENABLE_SENTRY by envBoolean("ENABLE_SENTRY")
    override val SMART_HOME_TYPE by envEnum<SmartHomeAdapter.Type>("SMART_HOME_TYPE")
    override val ENABLE_SENSOR_STATION by envBoolean("ENABLE_SENSOR_STATION", defaultValue = true)
    override val MQTT_HOST: String? by envString("MQTT_HOST")
    override val MQTT_PORT: Int by envInt("MQTT_PORT", defaultValue = 1883)
    override val MQTT_TLS_ENABLED: Boolean by envBoolean("MQTT_TLS_ENABLED")
    override val MQTT_CLIENT_ID: String by envString("MQTT_CLIENT_ID", defaultValue = "fama")
    override val MQTT_USERNAME: String? by envString("MQTT_USERNAME")
    override val MQTT_PASSWORD: String? by envString("MQTT_PASSWORD")
    override val MQTT_ENABLE: Boolean = SMART_HOME_TYPE != SmartHomeAdapter.Type.IB
    override val HOME_ASSISTANT_DISCOVERY_PREFIX: String by envString("HOME_ASSISTANT_DISCOVERY_PREFIX", defaultValue = "homeassistant")
    override val IO_BROKER_URL: String by envString("IO_BROKER_URL", defaultValue = "http://localhost:8087/v1/")
    override val IO_BROKER_PREFIX: String = "fama"
    override val IO_BROKER_STATION_FOLDER_PREFIX: String = "sensor-station"
}
