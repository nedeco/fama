package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.logger.logger
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.Settings
import io.sentry.Sentry
import io.sentry.kotlin.SentryContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.withContext

class FamaApplication: KoinComponent {
    private val mqttManager: MqttManager by inject()
    val settings: Settings by inject()
    private val logger by logger()

    init {
        logger.i { "Start" }
        if (settings.ENABLE_SENTRY && settings.SENTRY_DSN !is Nothing) {
            setupSentry()
        }
    }

    private fun setupSentry() {
        Sentry.init { options ->
            options.dsn = settings.SENTRY_DSN
            options.tracesSampleRate = 1.0
            options.profilesSampleRate = 0.2
            options.release = "fama@${settings.VERSION}"
        }
        Sentry.configureScope { scope ->
            scope.setContexts(
                "Settings",
                mapOf(
                    "Debug" to settings.DEBUG,
                    "MQTT TLS Enabled" to settings.MQTT_TLS_ENABLED,
                    "MQTT Port" to settings.MQTT_PORT,
                    "Sensor Station enabled" to settings.ENABLE_SENSOR_STATION,
                    "Home Assistant discovery prefix" to settings.HOME_ASSISTANT_DISCOVERY_PREFIX,
                    "Smart home type" to settings.SMART_HOME_TYPE,
                ),
            )
        }
        logger.i("Sentry is enabled")
    }

    @Throws(EnvVarMissingException::class)
    suspend fun start() = withContext(Dispatchers.Default + SentryContext()) {
        logger.i { "Start Fama" }
        if (settings.MQTT_ENABLE ) {
            mqttManager.start()
        }
        TwinMessageManager.start()

        val mqtt = async {
            if (settings.MQTT_ENABLE ) {
                mqttManager.listen()
            }
        }

        val twinMessage = async {
            if (settings.ENABLE_SENSOR_STATION) {
                TwinMessageManager.listenSensors()
            }
        }

        awaitAll(mqtt, twinMessage)

        logger.i { "Shutdown" }

        TwinMessageManager.stop()
        if (settings.MQTT_ENABLE ) {
            mqttManager.stop()
        }
    }
}
