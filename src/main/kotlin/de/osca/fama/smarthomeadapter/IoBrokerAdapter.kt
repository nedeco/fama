package de.osca.fama.smarthomeadapter

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.logger.logger
import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import de.osca.fama.smarthomeadapter.iobroker.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class IoBrokerAdapter: SmartHomeAdapter, KoinComponent {
    private val settings: Settings by inject()
    private val httpClient: HttpClient by inject()
    private val logger by logger()

    override suspend fun updateSensorStation(sensor: Sensor) {
        logger.d("Start Sending: ${sensor.sensorType.name}")
        ensureFolderStructure()
        ensureStation(sensor = sensor)
        ensureSensor(sensor = sensor)
        updateSensorValue(sensor = sensor)
    }

    private suspend fun ensureFolderStructure() {
        val famaUrl = restUrl(IoBrokerApiCommandType.OBJECT, settings.IO_BROKER_PREFIX)
        if (!httpClient.get(famaUrl).status.isSuccess()) {
            val baseFolderPayload = IoBrokerObjectPayload.from(
                name = settings.IO_BROKER_PREFIX,
                type = IoBrokerObjectType.FOLDER,
                commonType = IoBrokerCommonType.FOLDER
            )
            val baseFolderPayloadString = json.encodeToString(baseFolderPayload)
            httpClient.post(famaUrl) {
                contentType(ContentType.Application.Json)
                setBody(baseFolderPayloadString)
            }
        }
        if (!httpClient.get("$famaUrl.${settings.IO_BROKER_STATION_FOLDER_PREFIX}").status.isSuccess()) {
            val baseFolderPayload = IoBrokerObjectPayload.from(
                name = settings.IO_BROKER_STATION_FOLDER_PREFIX,
                type = IoBrokerObjectType.FOLDER,
                commonType = IoBrokerCommonType.FOLDER
            )
            val baseFolderPayloadString = json.encodeToString(baseFolderPayload)
            httpClient.post("$famaUrl.${settings.IO_BROKER_STATION_FOLDER_PREFIX}") {
                contentType(ContentType.Application.Json)
                setBody(baseFolderPayloadString)
            }
        }
    }

    private suspend fun ensureStation(sensor: Sensor) {
        val stationUrl = restUrl(IoBrokerApiCommandType.OBJECT, "${settings.IO_BROKER_PREFIX}.${settings.IO_BROKER_STATION_FOLDER_PREFIX}" + ".${sensor.station.objectId}")
        val response = httpClient.get(stationUrl)
        val stationPayload = IoBrokerObjectPayload.from(
            name = sensor.station.name,
            type = IoBrokerObjectType.FOLDER,
            commonType = IoBrokerCommonType.FOLDER
        )
        val stationPayloadString = json.encodeToString(stationPayload)
        if (response.status.isSuccess()) {
            val responsePayload = json.decodeFromString<IoBrokerObjectPayload>(response.bodyAsText())
            if (responsePayload.common.name != stationPayload.common.name) {
                httpClient.put(stationUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(stationPayloadString)
                }
            }
        } else {
            httpClient.post(stationUrl) {
                contentType(ContentType.Application.Json)
                setBody(stationPayloadString)
            }
        }
    }

    private suspend fun ensureIcon(sensor: Sensor): String? {
        val icon =  sensor.sensorType.icon
        if (icon != null) {
                val iconResponse = httpClient.get(icon)
                if (iconResponse.status.isSuccess()) {
                    val iconBase64 = Base64.getEncoder().encodeToString(iconResponse.body<ByteArray>())
                    val contentType = ContentType.defaultForFileExtension(icon)
                    return "data:${contentType.contentType}/${contentType.contentSubtype};base64,$iconBase64"
            }
        }
        return null
    }

    private suspend fun ensureSensor(sensor: Sensor) {
        val sensorUrl = restUrl(IoBrokerApiCommandType.OBJECT, "${settings.IO_BROKER_PREFIX}.${settings.IO_BROKER_STATION_FOLDER_PREFIX}.${sensor.station.objectId}.${sensor.objectId}")
        val response = httpClient.get(sensorUrl)
        val icon = ensureIcon(sensor = sensor)
        val sensorPayload = IoBrokerObjectPayload.from(
            name = sensor.sensorType.name,
            unit = sensor.sensorType.unit,
            type = IoBrokerObjectType.STATE,
            commonType = IoBrokerCommonType.NUMBER,
            icon = icon
        )
        val sensorPayloadString = json.encodeToString(sensorPayload)
        if (response.status.isSuccess()) {
            val responsePayload = json.decodeFromString<IoBrokerObjectPayload>(response.bodyAsText())
            if (responsePayload.common.name != sensorPayload.common.name || responsePayload.common.icon != sensorPayload.common.icon) {
                httpClient.put(sensorUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(sensorPayloadString)
                }
            }
        } else {
            httpClient.post(sensorUrl) {
                contentType(ContentType.Application.Json)
                setBody(sensorPayloadString)
            }
        }
    }

    private suspend fun updateSensorValue(sensor: Sensor) {
        val sensorUrl = restUrl(IoBrokerApiCommandType.STATE, "${settings.IO_BROKER_PREFIX}.${settings.IO_BROKER_STATION_FOLDER_PREFIX}.${sensor.station.objectId}.${sensor.objectId}")
        val sensorStatePayload = IoBrokerStatePayload(value = sensor.value)
        val sensorStatePayloadString =   json.encodeToString(sensorStatePayload)
        httpClient.patch(sensorUrl) {
            contentType(ContentType.Application.Json)
            setBody(sensorStatePayloadString)
        }
    }

    private fun restUrl(commandType: IoBrokerApiCommandType, suffix: String): String = "${settings.IO_BROKER_URL}${commandType.name.lowercase()}/$suffix"

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val json =
            Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                explicitNulls = false
                ignoreUnknownKeys = true
            }
    }
}
