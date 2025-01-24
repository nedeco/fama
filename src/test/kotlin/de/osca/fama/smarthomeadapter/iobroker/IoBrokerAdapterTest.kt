package de.osca.fama.smarthomeadapter.iobroker

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.digitaltwin.model.sensor.SensorStation
import de.osca.fama.digitaltwin.model.sensor.SensorType
import de.osca.fama.digitaltwin.model.sensor.SensorTypeCategory
import de.osca.fama.smarthomeadapter.IoBrokerAdapter
import de.osca.fama.smarthomeadapter.mockModules
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class IoBrokerAdapterTest : KoinTest {
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
    private val interceptor: IoBrokerMockEngineInterceptor by inject()
    private val ioBrokerAdapter: IoBrokerAdapter = IoBrokerAdapter()

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
        interceptor.sensor = testSensor
        interceptor.calls.clear()
    }

    @Test
    fun testIoBrokerAdapterFolderCreation() = runBlocking {
        interceptor.interceptRequest = {
            var response: Pair<String?, HttpStatusCode>? = null
            if (it.url.toString() == interceptor.folderUrl) {
                if (interceptor.calls.size == 1 && it.method == HttpMethod.Get) {
                    response = Pair(null, HttpStatusCode.NotFound)
                }
                if (interceptor.calls.size == 2 && it.method == HttpMethod.Post) {
                    response = Pair(null, HttpStatusCode.OK)
                }
            }
            if (it.url.toString() == interceptor.stationFolderUrl) {
                if (interceptor.calls.size == 3 && it.method == HttpMethod.Get) {
                    response = Pair(null, HttpStatusCode.NotFound)
                }
                if (interceptor.calls.size == 4 && it.method == HttpMethod.Post) {
                    response = Pair(null, HttpStatusCode.OK)
                }
            }
            response
        }
        val expectedCalls =
            listOf(
                Pair(interceptor.folderUrl, HttpMethod.Get),
                Pair(interceptor.folderUrl, HttpMethod.Post),
                Pair(interceptor.stationFolderUrl, HttpMethod.Get),
                Pair(interceptor.stationFolderUrl, HttpMethod.Post),
            )

        ioBrokerAdapter.updateSensorStation(testSensor)
        assert(compareCalls(expectedCalls))
    }

    @Test
    fun testIoBrokerAdapterStationCreation() = runBlocking {
        interceptor.interceptRequest = {
            var response: Pair<String?, HttpStatusCode>? = null
            if (it.url.toString() == interceptor.stationUrl(testSensor.station.objectId)) {
                if (interceptor.calls.size == 3 && it.method == HttpMethod.Get) {
                    response = Pair(null, HttpStatusCode.NotFound)
                }
                if (interceptor.calls.size == 4 && it.method == HttpMethod.Post) {
                    response = Pair(null, HttpStatusCode.OK)
                }
            }
            response
        }
        val expectedCalls =
            listOf(
                null,
                null,
                Pair(interceptor.stationUrl(testSensor.station.objectId), HttpMethod.Get),
                Pair(interceptor.stationUrl(testSensor.station.objectId), HttpMethod.Post),
            )
        ioBrokerAdapter.updateSensorStation(testSensor)
        assert(compareCalls(expectedCalls))
    }

    @Test
    fun testIoBrokerAdapterSensorCreation() = runBlocking {
        interceptor.interceptRequest = {
            var response: Pair<String?, HttpStatusCode>? = null
            if (it.url.toString() == interceptor.sensorUrl(testSensor.station.objectId, testSensor.objectId)) {
                if (interceptor.calls.size == 4 && it.method == HttpMethod.Get) {
                    response = Pair(null, HttpStatusCode.NotFound)
                }
                if (interceptor.calls.size == 5 && it.method == HttpMethod.Post) {
                    response = Pair(null, HttpStatusCode.OK)
                }
            }
            response
        }
        val expectedCalls =
            listOf(
                null,
                null,
                null,
                Pair(interceptor.sensorUrl(testSensor.station.objectId, testSensor.objectId), HttpMethod.Get),
                Pair(interceptor.sensorUrl(testSensor.station.objectId, testSensor.objectId), HttpMethod.Post),
            )
        ioBrokerAdapter.updateSensorStation(testSensor)
        assert(compareCalls(expectedCalls))
    }

    @Test
    fun testIoBrokerAdapterSensorValue() = runBlocking {
        var value: Double? = null
        val testSensor2 = testSensor.copy(value = testSensor.value + 1.0)
        interceptor.interceptRequest = {
            var response: Pair<String?, HttpStatusCode>? = null
            if (it.url.toString() == interceptor.sensorStateUrl(testSensor.station.objectId, testSensor.objectId)) {
                if (interceptor.calls.size in listOf(5, 10) && it.method == HttpMethod.Patch) {
                    runBlocking {
                        value = IoBrokerAdapter.json.decodeFromString<IoBrokerStatePayload>(it.body.toByteArray().decodeToString()).value
                    }
                    response = Pair(null, HttpStatusCode.OK)
                }
            }
            response
        }
        val expectedCalls =
            listOf(
                null,
                null,
                null,
                null,
                Pair(interceptor.sensorStateUrl(testSensor.station.objectId, testSensor.objectId), HttpMethod.Patch),
                null,
                null,
                null,
                null,
                Pair(interceptor.sensorStateUrl(testSensor.station.objectId, testSensor.objectId), HttpMethod.Patch),
            )
        ioBrokerAdapter.updateSensorStation(testSensor)
        assert(value == testSensor.value)
        ioBrokerAdapter.updateSensorStation(testSensor2)
        assert(value == testSensor2.value)
        assert(compareCalls(expectedCalls))
    }

    private fun compareCalls(expectedCalls: List<Pair<String, HttpMethod>?>): Boolean {
        for (i in expectedCalls.indices) {
            if (expectedCalls[i] != null && expectedCalls[i] != interceptor.calls[i]) {
                return false
            }
        }
        return true
    }
}
