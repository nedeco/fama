package de.osca.fama.smarthomeadapter

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.digitaltwin.model.sensor.SensorTypeCategory
import de.osca.fama.generated.BuildConfig
import de.osca.fama.logger.logger
import de.osca.fama.settings.Settings
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantComponent
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantDevice
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantDeviceClass
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantOrigin
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantPayload
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantTopicType
import io.github.davidepianca98.mqtt.packets.Qos
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

class HomeAssistantAdapter : SmartHomeAdapter {
    private val logger by logger()
    private val configuredComponents = mutableSetOf<String>()

    override suspend fun updateSensorStation(sensor: Sensor) {
        logger.d("Start Sending: ${sensor.sensorType.name}")
        if (!configuredComponents.contains(sensor.objectId)) {
            createSensor(sensor)
            configuredComponents.add(sensor.objectId)
            // Give the System some time to react
            // If not it is fine
            delay(1000)
        }
        createSensorState(sensor)
    }

    private fun createSensorState(sensor: Sensor) {
        mqtt.publish(
            topic(
                HomeAssistantComponent.SENSOR,
                sensor.objectId,
                HomeAssistantTopicType.STATE,
            ),
            sensor.value.toString(),
        )
    }

    private fun createSensor(sensor: Sensor) {
        val deviceClass =
            HomeAssistantDeviceClass.entries.find {
                it.name == sensor.sensorType.name
            }

        val icon =
            when (sensor.sensorType.type) {
                SensorTypeCategory.WIND_DIRECTION -> "mdi:compass-rose"
                SensorTypeCategory.SALINITY -> "mdi:water-plus"
                SensorTypeCategory.UV_INDEX -> "mdi:weather-sunny"
                SensorTypeCategory.WATER_FILM_HEIGHT -> "water-opacity"
                SensorTypeCategory.SNOW_HEIGHT -> "mdi:snowflake"
                SensorTypeCategory.ROAD_CONDITION -> "mdi:road-variant"
                SensorTypeCategory.FRICTION_COEFFICIENT -> "mdi:car-traction-control"
                else -> null
            }

        val homeAssistantPayload =
            HomeAssistantPayload(
                uniqueId = sensor.objectId,
                name = sensor.sensorType.name,
                icon = icon,
                unitOfMeasurement = sensor.sensorType.unit,
                deviceClass = deviceClass,
                stateTopic = topic(HomeAssistantComponent.SENSOR, sensor.objectId, HomeAssistantTopicType.STATE),
                device =
                    HomeAssistantDevice(
                        identifiers = sensor.station.objectId,
                        name = sensor.station.name,
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
        val jsonString = json.encodeToString(homeAssistantPayload)
        logger.d("Sending Config Payload: $jsonString")

        mqtt.publish(
            topic(
                HomeAssistantComponent.SENSOR,
                sensor.objectId,
                HomeAssistantTopicType.CONFIG,
            ),
            jsonString,
            qos = Qos.EXACTLY_ONCE,
            retain = true,
        )
    }

    companion object {
        private val discoveryPrefix = Settings.HOME_ASSISTANT_DISCOVERY_PREFIX

        @OptIn(ExperimentalSerializationApi::class)
        private val json =
            Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
                explicitNulls = false
            }

        private fun topic(
            component: HomeAssistantComponent,
            objectId: String,
            type: HomeAssistantTopicType,
        ) = "$discoveryPrefix/${component.name.lowercase()}/$objectId/${type.name.lowercase()}"
    }
}
