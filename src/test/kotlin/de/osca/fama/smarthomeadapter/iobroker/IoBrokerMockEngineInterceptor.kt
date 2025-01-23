package de.osca.fama.smarthomeadapter.iobroker

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.settings.Settings
import de.osca.fama.smarthomeadapter.IoBrokerAdapter
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IoBrokerMockEngineInterceptor : KoinComponent {
    private val settings: Settings by inject()
    val famaFolderUrl = "${settings.ioBrokerUrl}${IoBrokerApiCommandType.OBJECT.name.lowercase()}/${settings.IO_BROKER_PREFIX}"
    val stationFolderUrl = "${settings.ioBrokerUrl}${IoBrokerApiCommandType.OBJECT.name.lowercase()}/${settings.IO_BROKER_PREFIX}.${settings.IO_BROKER_STATION_FOLDER_PREFIX}"
    var sensor: Sensor? = null
    var interceptRequest: MockRequestHandleScope.(HttpRequestData) -> Pair<String?, HttpStatusCode>? = { null }
    val calls = mutableListOf<Pair<String, HttpMethod>?>()
    val mockEngine: MockEngine =
        MockEngine { request ->
            calls += Pair(request.url.toString(), request.method)
            interceptRequest(request)?.let {
                return@MockEngine respond(
                    it.first ?: "",
                    it.second,
                    headers =
                        headersOf(
                            "Content-Type" to listOf(ContentType.Application.Json.toString()),
                        ),
                )
            }
            when (request.url.toString()) {
                famaFolderUrl, stationFolderUrl -> {
                    if (request.method == HttpMethod.Get || request.method == HttpMethod.Post) {
                        return@MockEngine respond("", HttpStatusCode.OK)
                    }
                }
                stationUrl(sensor?.station?.objectId ?: "") -> {
                    if (request.method == HttpMethod.Get || request.method == HttpMethod.Post) {
                        val stationPayload =
                            IoBrokerObjectPayload.from(
                                name = sensor?.station?.name ?: "",
                                type = IoBrokerObjectType.FOLDER,
                                commonType = IoBrokerCommonType.FOLDER,
                            )
                        return@MockEngine respond(
                            IoBrokerAdapter.json.encodeToString(stationPayload),
                            HttpStatusCode.OK,
                            headers =
                                headersOf(
                                    "Content-Type" to listOf(ContentType.Application.Json.toString()),
                                ),
                        )
                    }
                }
                sensorUrl(sensor?.station?.objectId ?: "", sensor?.objectId ?: "") -> {
                    if (request.method == HttpMethod.Get || request.method == HttpMethod.Post) {
                        val sensorPayload =
                            IoBrokerObjectPayload.from(
                                name = sensor?.sensorType?.name ?: "",
                                type = IoBrokerObjectType.STATE,
                                commonType = IoBrokerCommonType.NUMBER,
                            )
                        return@MockEngine respond(
                            IoBrokerAdapter.json.encodeToString(sensorPayload),
                            HttpStatusCode.OK,
                            headers =
                                headersOf(
                                    "Content-Type" to listOf(ContentType.Application.Json.toString()),
                                ),
                        )
                    }
                }
                sensorStateUrl(sensor?.station?.objectId ?: "", sensor?.objectId ?: "") -> {
                    if (request.method == HttpMethod.Patch) {
                        return@MockEngine respond("", HttpStatusCode.OK)
                    }
                }
            }
            return@MockEngine respond("", HttpStatusCode.InternalServerError)
        }

    fun stationUrl(stationId: String) = "$stationFolderUrl.$stationId"

    fun sensorUrl(
        stationId: String,
        sensorId: String,
    ) = "$stationFolderUrl.$stationId.$sensorId"

    fun sensorStateUrl(
        stationId: String,
        sensorId: String,
    ) =
        "${settings.ioBrokerUrl}${IoBrokerApiCommandType.STATE.name.lowercase()}/${settings.IO_BROKER_PREFIX}.${settings.IO_BROKER_STATION_FOLDER_PREFIX}.$stationId.$sensorId"
}
