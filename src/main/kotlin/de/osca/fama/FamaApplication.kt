package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.logger.logger
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.Settings
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import io.sentry.Sentry
import io.sentry.kotlin.SentryContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object FamaApplication: KoinComponent {
    private val mqttManager: MqttManager by inject()
    val settings: Settings by inject()
    private val twinMessageManager: TwinMessageManager by inject()
    private val smartHomeAdapter: SmartHomeAdapter by inject()
    private val mqttManager: MqttManager by inject()
    private val logger by logger()

    init {
        logger.i { "Start" }
        if (settings.enableSentry && settings.SENTRY_DSN !is Nothing) {
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
                    "Debug" to settings.debug,
                    "MQTT TLS Enabled" to settings.mqttTlsEnabled,
                    "MQTT Port" to settings.mqttPort,
                    "Sensor Station enabled" to settings.enableSensorStation,
                    "Home Assistant discovery prefix" to settings.homeAssistantDiscoveryPrefix,
                    "Smart home type" to settings.smartHomeType,
                ),
            )
        }
        logger.i("Sentry is enabled")
    }

    @Throws(EnvVarMissingException::class)
    suspend fun start() = withContext(Dispatchers.Default + SentryContext()) {
        logger.i { "Start Fama" }
        if (smartHomeAdapter.mqttEnabled) {
            mqttManager.start()
        }
        twinMessageManager.start()

        val twinMessage =
            async {
                if (settings.enableSensorStation) {
                    twinMessageManager.listenSensors()
                }
            }

        twinMessage.await()

        logger.i { "Shutdown" }
        twinMessageManager.stop()

        if (smartHomeAdapter.mqttEnabled) {
            mqttManager.stop()
        }
    }
}
