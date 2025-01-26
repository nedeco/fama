package de.osca.fama.smarthomeadapter.settings

import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.SettingsImpl
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsTest {
    private val envMap: MutableMap<String, String> by lazy {
        val classOfMap: Class<*> = System.getenv().javaClass
        val field: Field = classOfMap.getDeclaredField("m")
        field.isAccessible = true
        return@lazy field.get(System.getenv()) as MutableMap<String, String>
    }

    @BeforeEach
    fun setUp() {
        envMap.clear()
    }

    @Test
    fun testEnvVarMissingException() {
        assertFailsWith<EnvVarMissingException> {
            SettingsImpl().ioBrokerUrl
        }
    }

    @Test
    fun testDebug() {
        envMap["DEBUG"] = "true"
        val settings = SettingsImpl()
        assertTrue(settings.debug)
    }

    @Test
    fun testEnableSentry() {
        envMap["ENABLE_SENTRY"] = "false"
        val settings = SettingsImpl()
        assertFalse(settings.enableSentry)
    }

    @Test
    fun testSmartHomeType() {
        envMap["SMART_HOME_TYPE"] = "IB"
        val settings = SettingsImpl()
        assertEquals(SmartHomeAdapter.Type.IB, settings.smartHomeType)
    }

    @Test
    fun testEnableSensorStation() {
        envMap["ENABLE_SENSOR_STATION"] = "true"
        val settings = SettingsImpl()
        assertTrue(settings.enableSensorStation)
    }

    @Test
    fun testMqttHost() {
        envMap["MQTT_HOST"] = "localhost"
        val settings = SettingsImpl()
        assertEquals("localhost", settings.mqttHost)
    }

    @Test
    fun testMqttPort() {
        envMap["MQTT_PORT"] = "1883"
        val settings = SettingsImpl()
        assertEquals(1883, settings.mqttPort)
    }

    @Test
    fun testMqttTlsEnabled() {
        envMap["MQTT_TLS_ENABLED"] = "false"
        val settings = SettingsImpl()
        assertFalse(settings.mqttTlsEnabled)
    }

    @Test
    fun testMqttClientId() {
        envMap["MQTT_CLIENT_ID"] = "fama"
        val settings = SettingsImpl()
        assertEquals("fama", settings.mqttClientId)
    }

    @Test
    fun testMqttUsername() {
        envMap["MQTT_USERNAME"] = "user"
        val settings = SettingsImpl()
        assertEquals("user", settings.mqttUsername)
    }

    @Test
    fun testMqttPassword() {
        envMap["MQTT_PASSWORD"] = "password"
        val settings = SettingsImpl()
        assertEquals("password", settings.mqttPassword)
    }

    @Test
    fun testHomeAssistantDiscoveryPrefix() {
        envMap["HOME_ASSISTANT_DISCOVERY_PREFIX"] = "homeassistant"
        val settings = SettingsImpl()
        assertEquals("homeassistant", settings.homeAssistantDiscoveryPrefix)
    }

    @Test
    fun testIoBrokerUrl() {
        envMap["IO_BROKER_URL"] = "http://localhost:8087/v1/"
        val settings = SettingsImpl()
        assertEquals("http://localhost:8087/v1/", settings.ioBrokerUrl)
    }

    @Test
    fun testIoBrokerPrefix() {
        envMap["IO_BROKER_PREFIX"] = "fama"
        val settings = SettingsImpl()
        assertEquals("fama", settings.ioBrokerPrefix)
    }

    @Test
    fun testIoBrokerStationFolderPrefix() {
        envMap["IO_BROKER_STATION_FOLDER_PREFIX"] = "sensor-station"
        val settings = SettingsImpl()
        assertEquals("sensor-station", settings.ioBrokerStationFolderPrefix)
    }
}

/*
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
 */
