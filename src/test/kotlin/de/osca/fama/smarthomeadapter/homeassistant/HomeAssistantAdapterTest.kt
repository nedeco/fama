package de.osca.fama.smarthomeadapter.homeassistant

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.digitaltwin.model.sensor.SensorStation
import de.osca.fama.digitaltwin.model.sensor.SensorType
import de.osca.fama.digitaltwin.model.sensor.SensorTypeCategory
import de.osca.fama.generated.BuildConfig
import de.osca.fama.smarthomeadapter.HomeAssistantAdapter
import de.osca.fama.smarthomeadapter.mockModules
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class HomeAssistantAdapterTest : KoinTest {
    private val testSensor =
        Sensor(
            objectId = "sensorId",
            value = 25.0,
            refId = "refId",
            station = SensorStation("stationId", "stationName", Clock.System.now(), Clock.System.now()),
            sensorType =
                SensorType(
                    "sensorTypeId",
                    "sensorTypeName",
                    "definition",
                    SensorTypeCategory.TEMPERATURE,
                    "°C",
                    null,
                    0,
                    Clock.System.now(),
                    Clock.System.now(),
                ),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        )

    private val mqttManager: HomeAssistantMockMqttManager by inject()
    private val homeAssistantAdapter: HomeAssistantAdapter = HomeAssistantAdapter()

    @JvmField
    @RegisterExtension
    val koinTestExtension =
        KoinTestExtension.create {
            modules(
                mockModules,
            )
        }

    @BeforeEach
    fun setUp() {
        mqttManager.messages.clear()
        homeAssistantAdapter.configuredComponents.clear()
    }

    @Test
    fun testSensorCreation() = runBlocking {
        val homeAssistantPayload =
            HomeAssistantPayload(
                uniqueId = testSensor.objectId,
                name = testSensor.sensorType.name,
                icon = null,
                unitOfMeasurement = testSensor.sensorType.unit,
                deviceClass =
                    HomeAssistantDeviceClass.entries.find {
                        it.name == testSensor.sensorType.name
                    },
                stateTopic =
                    homeAssistantAdapter.topic(
                        HomeAssistantComponent.SENSOR,
                        testSensor.objectId,
                        HomeAssistantTopicType.STATE,
                    ),
                device =
                    HomeAssistantDevice(
                        identifiers = testSensor.station.objectId,
                        name = testSensor.station.name,
                        swVersion = BuildConfig.VERSION,
                        configurationUrl = BuildConfig.SUPPORT_URL,
                        model = "Sensor Station",
                    ),
                origin =
                    HomeAssistantOrigin(
                        name = "Fama",
                        swVersion = BuildConfig.VERSION,
                        supportUrl = BuildConfig.SUPPORT_URL,
                    ),
            )

        homeAssistantAdapter.updateSensorStation(testSensor)
        assert(homeAssistantAdapter.configuredComponents.contains(testSensor.objectId))
        assert(json.decodeFromString<HomeAssistantPayload>(mqttManager.messages[0].second) == homeAssistantPayload)
        assert(mqttManager.messages.size == 2)
        assert(
            mqttManager.messages[0].first ==
                homeAssistantAdapter.topic(
                    HomeAssistantComponent.SENSOR,
                    testSensor.objectId,
                    HomeAssistantTopicType.CONFIG,
                ),
        )
    }

    @Test
    fun testSensorStateCreation() = runBlocking {
        homeAssistantAdapter.updateSensorStation(testSensor)
        assert(
            mqttManager.messages[1].first ==
                homeAssistantAdapter.topic(
                    HomeAssistantComponent.SENSOR,
                    testSensor.objectId,
                    HomeAssistantTopicType.STATE,
                ),
        )
        assert(mqttManager.messages[1].second == testSensor.value.toString())
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val json =
            Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
                explicitNulls = false
            }
    }
}
