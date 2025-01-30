package de.osca.fama.mqtt

import de.osca.fama.logger.logger
import io.github.davidepianca98.mqtt.broker.Broker
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.MQTT5Properties
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class MqttManagerBrokerImpl :
    MqttManager,
    KoinComponent {
    private val logger by logger()
    private lateinit var broker: Broker

    @OptIn(DelicateCoroutinesApi::class)
    override fun start() {
        broker = Broker(authentication = EnvAuthentication)
        logger.i("MQTT Broker Started")
        GlobalScope.async {
            listen()
        }
    }

    private suspend fun listen(): Unit =
        withContext(Dispatchers.IO) {
            broker.listen()
        }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun publish(
        topic: String,
        payload: String,
        qos: Qos,
        retain: Boolean,
    ) {
        logger.d { "Publish Payload: $payload" }
        broker.publish(retain, topic, qos, MQTT5Properties(), payload.encodeToByteArray().toUByteArray())
        logger.d { "Publish Successfully" }
    }

    override fun stop() {
        broker.stop()
    }
}
