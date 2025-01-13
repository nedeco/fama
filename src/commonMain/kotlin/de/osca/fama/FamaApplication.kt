package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.logger.logger
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.Settings
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

object FamaApplication {
    private val logger by logger()

    fun start() = runBlocking {
        logger.i { "Start Fama" }
        MqttManager.start()
        TwinMessageManager.start()

        val mqtt = async {
            MqttManager.listen()
        }

        val twinMessage = async {
            if (Settings.ENABLE_SENSOR_STATION) {
                TwinMessageManager.listenSensors()
            }
        }

        try {
            awaitAll(mqtt, twinMessage)
        } catch (e: Exception) {
            logger.e("Unexpected Error:", e)
        }
        logger.i { "Shutdown Fama" }

        TwinMessageManager.stop()
        MqttManager.stop()
    }
}
