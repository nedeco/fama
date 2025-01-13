package de.osca.fama.digitaltwin

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.generated.BuildConfig
import de.osca.fama.logger.logger
import de.osca.fama.settings.Settings
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.stomp.config.StompConfig
import org.hildan.krossbow.stomp.headers.AckMode
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import org.hildan.krossbow.websocket.reconnection.withAutoReconnect
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

object TwinMessageManager {
    private val logger by logger()
    private val smartHomeAdapter = SmartHomeAdapter.getAdapter(Settings.SMART_HOME_TYPE)
    private lateinit var session: StompSession
    private val dispatcher: CoroutineContext = Dispatchers.IO

    private val subscriptions = mutableListOf<Job>()

    private val stompConfig = StompConfig().apply {
        connectionTimeout = 15.seconds
        heartBeat = HeartBeat(
            minSendPeriod = 60.seconds,
            expectedPeriod = 60.seconds
        )
        heartBeatTolerance = HeartBeatTolerance(
            incomingMargin = 20.seconds,
            outgoingMargin = 20.seconds,
        )
    }

    private val oneHourTimeStampFromNow by lazy {
        Clock.System.now().minus(1.hours).epochSeconds.toString()
    }

    suspend fun start() {
        logger.i { "Stomp Client Start Connecting to Server" }
        session = StompClient(
            KtorWebSocketClient().withAutoReconnect(),
            stompConfig
        ).connect(
            BuildConfig.RABBIT_MQ_STOMP_URL,
            BuildConfig.RABBIT_MQ_STOMP_USERNAME,
            BuildConfig.RABBIT_MQ_STOMP_PASSWORD,
            "/"
        )
        logger.i { "Stomp Client Connected to Server" }
    }

    suspend fun stop() {
        subscriptions.forEach { it.cancelAndJoin() }
        session.disconnect()
    }

    suspend fun listenSensors() = withContext(dispatcher) {
        subscriptions.add(launch {
            logger.i { "Start Subscribing to Sensors" }
            val header = StompSubscribeHeaders(
                "/amq/queue/s.public.sensor"
            ) {
                ack = AckMode.CLIENT
                set("prefetch-count", "50")
                set("x-stream-offset", "1h")
                set("timestamp", oneHourTimeStampFromNow)
            }

            val subscription = session.subscribeAndAutoAck<DigitalTwinMessage<Sensor>>(
                header
            )
            logger.i("Subscribed to Sensors")
            subscription.collect { message ->
                if (!checkVersions(message)) return@collect
                smartHomeAdapter.updateSensorStation(message.payload)
            }
        })
    }

    private fun checkVersions(message: DigitalTwinMessage<*>, schemaVersion: Int = 1, messageVersion: Int = 1, ): Boolean {
        return message.schemaVersion == schemaVersion && message.messageVersion == messageVersion
    }
}

val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
}

private suspend inline fun <reified T : Any> StompSession.subscribeAndAutoAck(header: StompSubscribeHeaders): Flow<T> {
    return subscribe(header).map { message ->
        val entity = json.decodeFromString<T>(message.bodyAsText)
        message.headers.ack?.let { ack(it) }
        return@map entity
    }
}
