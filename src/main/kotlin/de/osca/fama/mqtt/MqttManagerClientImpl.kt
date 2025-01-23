package de.osca.fama.mqtt

import co.touchlab.kermit.Severity
import de.osca.fama.logger.logger
import de.osca.fama.settings.Settings
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.ReasonCode
import io.github.davidepianca98.socket.tls.TLSClientSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MqttManagerClientImpl(
    private val mqttHost: String,
) : MqttManager,
    KoinComponent {
    private val settings: Settings by inject()
    private val logger by logger()
    private lateinit var client: MQTTClient

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        client =
            MQTTClient(
                mqttVersion = MQTTVersion.MQTT5,
                address = mqttHost,
                port = settings.mqttPort,
                tls = if (settings.mqttTlsEnabled) TLSClientSettings() else null,
                clientId = settings.mqttClientId,
                userName = settings.mqttUsername,
                password = settings.mqttPassword?.encodeToByteArray()?.toUByteArray(),
            ) {
                logger.i("client")
            }
        listen()
        logger.i("MQTT Client Started")
    }

    private fun listen(): Unit {
        client.runSuspend()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun publish(
        topic: String,
        payload: String,
        qos: Qos,
        retain: Boolean,
    ) {
        logger.d("Publish Payload: $payload")
        client.publish(retain, qos, topic, payload.encodeToByteArray().toUByteArray())
        logger.d("Publish Successfully")
    }

    override fun stop() {
        client.disconnect(ReasonCode.SUCCESS)
    }
}
