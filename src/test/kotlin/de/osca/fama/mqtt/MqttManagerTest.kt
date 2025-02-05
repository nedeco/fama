package de.osca.fama.mqtt

import de.osca.fama.mockModules
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.Subscription
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.ReasonCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.Test
import kotlin.test.assertContains

class MqttManagerTest {
    private lateinit var broker: MqttManagerBrokerImpl
    private lateinit var client: MqttManagerClientImpl

    @JvmField
    @RegisterExtension
    val koinTestExtension =
        KoinTestExtension.create {
            modules(
                mockModules,
            )
            broker = MqttManagerBrokerImpl()
            client = MqttManagerClientImpl("localhost")
        }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun testMqttClientBrokerCommunication() =
        runTest {
            var clientConnected = false
            broker.start()
            client.start()

            val payloadList = mutableListOf<String>()

            val testClient =
                MQTTClient(
                    mqttVersion = MQTTVersion.MQTT5,
                    address = "localhost",
                    port = 1883,
                    tls = null,
                    clientId = null,
                    userName = "fama-username",
                    password = "fama-password".encodeToByteArray().toUByteArray(),
                    onConnected = { clientConnected = true },
                ) {
                    it.payload?.toByteArray()?.decodeToString()?.let { payload -> payloadList.add(payload) }
                }

            val topic = "test/topic"
            testClient.subscribe(listOf(Subscription(topic)))
            testClient.runSuspend()

            while (!clientConnected) {
                // wait on connection
                delay(100)
            }

            val payloadBroker = "Hello Broker, MQTT!"
            val payloadClient = "Hello Client, MQTT!"

            broker.publish(topic, payloadBroker, Qos.AT_LEAST_ONCE, retain = false)
            client.publish(topic, payloadClient, Qos.AT_LEAST_ONCE, retain = false)

            while (payloadList.size < 2) {
                // wait on messages
                delay(100)
            }

            assertContains(payloadList, payloadBroker)
            assertContains(payloadList, payloadClient)

            client.stop()
            broker.stop()
            testClient.disconnect(ReasonCode.SUCCESS)
        }
}
