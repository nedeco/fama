package de.osca.fama.mqtt

import de.osca.fama.logger.logger
import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.broker.Broker
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.MQTT5Properties
import io.github.davidepianca98.mqtt.packets.mqttv5.ReasonCode
import io.github.davidepianca98.socket.tls.TLSClientSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MqttManagerImpl: MqttManager, KoinComponent {
    private val settings: Settings by inject()
    private val logger by logger()
    private var broker: Broker? = null
    private var client: MQTTClient? = null

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        if (settings.MQTT_HOST.isNullOrBlank()) {
            broker = Broker(authentication = EnvAuthentication)
            logger.i("MQTT Broker Started")
        } else {
            client =
                MQTTClient(
                    mqttVersion = MQTTVersion.MQTT5,
                    address = settings.MQTT_HOST!!,
                    port = settings.MQTT_PORT,
                    tls = if (settings.MQTT_TLS_ENABLED) TLSClientSettings() else null,
                    clientId = settings.MQTT_CLIENT_ID,
                    userName = settings.MQTT_USERNAME,
                    password = settings.MQTT_PASSWORD?.encodeToByteArray()?.toUByteArray()
                ) {
                    println(it.payload?.toByteArray()?.decodeToString())
                }

            logger.i("MQTT Client Started")
        }
    }

    override suspend fun listen(): Unit = withContext(Dispatchers.IO) {
        broker?.listen()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun publish(
        topic: String,
        payload: String,
        qos: Qos,
        retain: Boolean,
    ) {
        logger.d { "Publish Payload: $payload" }
        if (client != null) {
            client!!.publish(retain, qos, topic, payload.encodeToByteArray().toUByteArray())
        } else if (broker != null) {
            broker!!.publish(retain, topic, qos, MQTT5Properties(), payload.encodeToByteArray().toUByteArray())
        }
        logger.d { "Publish Successfully" }
    }

    override fun stop() {
        if (client != null) {
            client!!.disconnect(ReasonCode.SUCCESS)
        } else if (broker != null) {
            broker!!.stop()
        }
    }
}
