package de.osca.fama

import co.touchlab.kermit.Logger
import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.logger.logger
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.Settings
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import io.sentry.Sentry
import io.sentry.kotlin.SentryContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object FamaApplication : KoinComponent {
    private val settings: Settings by inject()
    private val twinMessageManager: TwinMessageManager by inject()
    private val smartHomeAdapter: SmartHomeAdapter by inject()
    private val mqttManager: MqttManager by inject()
    private val logger by logger()

    @OptIn(DelicateCoroutinesApi::class)
    fun launch() = runBlocking {
        logger.i { "Start" }
        if (settings.enableSentry && settings.SENTRY_DSN !is Nothing) {
            setupSentry()
        }

        val job =
            GlobalScope.async {
                start()
            }

        runBlocking(SentryContext()) {
            try {
                job.await()
            } catch (e: Throwable) {
                handleError(e)
            }
        }
    }

    @Throws(EnvVarMissingException::class)
    private suspend fun start() = withContext(Dispatchers.Default + SentryContext()) {
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

    private fun handleError(error: Throwable) {
        when (val cause = error.cause) {
            is EnvVarMissingException -> {
                cause.message?.let {
                    Logger.e(it, tag = "ENV")
                }
            }
            else -> {
                if (FamaApplication.settings.debug) {
                    Logger.e("Unexpected Error -", error)
                } else {
                    Logger.e("Unexpected Error - ${error.cause?.message}")
                }
                Sentry.captureException(error)
            }
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
}
