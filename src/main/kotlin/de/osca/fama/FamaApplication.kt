package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.logger.logger
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.Settings
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

object FamaApplication {
    private val logger by logger()

    @Throws(EnvVarMissingException::class)
    fun start() = runBlocking {
        logger.i { "Start Fama" }

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

        logger.i { "Shutdown Fama" }

        TwinMessageManager.stop()
        MqttManager.stop()
    }
}
