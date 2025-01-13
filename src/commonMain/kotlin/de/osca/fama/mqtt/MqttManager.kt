package de.osca.fama.mqtt

import de.osca.fama.logger.logger
import de.osca.fama.settings.Settings
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.broker.Broker
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.MQTT5Properties
import io.github.davidepianca98.mqtt.packets.mqttv5.ReasonCode
import io.github.davidepianca98.socket.tls.TLSClientSettings
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

object MqttManager {
    private val logger by logger()

    private var broker: Broker? = null
    private var client: MQTTClient? = null
    @OptIn(ExperimentalUnsignedTypes::class)
    fun start() {
        if (Settings.MQTT_HOST.isNullOrBlank()) {
            broker = Broker(authentication = EnvAuthentication)
            logger.i("MQTT Broker Started")
        } else {
             client = MQTTClient(
                 mqttVersion = MQTTVersion.MQTT5,
                 address = Settings.MQTT_HOST!!,
                 port = Settings.MQTT_PORT,
                 tls = if (Settings.MQTT_TLS_ENABLED) TLSClientSettings() else null,
                 clientId = Settings.MQTT_CLIENT_ID,
                 userName = Settings.MQTT_USERNAME,
                 password = Settings.MQTT_PASSWORD?.encodeToByteArray()?.toUByteArray(),
             ) {
                 println(it.payload?.toByteArray()?.decodeToString())
             }

             logger.i("MQTT Client Started")
         }
    }

    suspend fun listen() = withContext(Dispatchers.IO) {
        broker?.listen()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun publish(
        topic: String,
        payload: String,
        qos: Qos = Qos.AT_MOST_ONCE,
        retain: Boolean = false
    ) {
        logger.d { "Publish Payload: $payload" }
        if (client != null) {
           client!!.publish(retain, qos, topic, payload.encodeToByteArray().toUByteArray())
        } else if (broker != null) {
            broker!!.publish(retain, topic, qos, MQTT5Properties(), payload.encodeToByteArray().toUByteArray())
        }
        logger.d { "Publish Successfully" }
    }

    fun stop() {
        if (client != null) {
           client!!.disconnect(ReasonCode.SUCCESS)
        } else if (broker != null) {
            broker!!.stop()
        }
    }
}
