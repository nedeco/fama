package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.generated.BuildConfig
import de.osca.fama.logger.logger
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.Settings
import io.sentry.Sentry
import io.sentry.kotlin.SentryContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object FamaApplication {
    private val logger by logger()

    init {
        logger.i { "Start" }
        if (Settings.ENABLE_SENTRY && BuildConfig.SENTRY_DSN !is Nothing) {
            setupSentry()
        }
    }

    private fun setupSentry() {
        Sentry.init { options ->
            options.dsn = BuildConfig.SENTRY_DSN
            options.tracesSampleRate = 1.0
            options.profilesSampleRate = 0.2
            options.release = "fama@${BuildConfig.VERSION}"
        }
        Sentry.configureScope { scope ->
            scope.setContexts(
                "Settings",
                mapOf(
                    "Debug" to Settings.DEBUG,
                    "MQTT TLS Enabled" to Settings.MQTT_TLS_ENABLED,
                    "MQTT Port" to Settings.MQTT_PORT,
                    "Sensor Station enabled" to Settings.ENABLE_SENSOR_STATION,
                    "Home Assistant discovery prefix" to Settings.HOME_ASSISTANT_DISCOVERY_PREFIX,
                    "Smart home type" to Settings.SMART_HOME_TYPE,
                ),
            )
        }
        logger.i("Sentry is enabled")
    }

    @Throws(EnvVarMissingException::class)
    suspend fun start() = withContext(Dispatchers.Default + SentryContext()) {
        MqttManager.start()
        TwinMessageManager.start()

        val mqtt =
            async {
                MqttManager.listen()
            }

        val twinMessage =
            async {
                if (Settings.ENABLE_SENSOR_STATION) {
                    TwinMessageManager.listenSensors()
                }
            }

        awaitAll(mqtt, twinMessage)

        logger.i { "Shutdown" }

        TwinMessageManager.stop()
        MqttManager.stop()
    }
}
