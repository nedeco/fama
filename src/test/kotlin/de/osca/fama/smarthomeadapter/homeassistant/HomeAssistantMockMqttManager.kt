package de.osca.fama.smarthomeadapter.homeassistant

import de.osca.fama.mqtt.MqttManager
import io.github.davidepianca98.mqtt.packets.Qos

class HomeAssistantMockMqttManager: MqttManager {
    var messages = mutableListOf<Pair<String, String>>()

    override fun start() {
        TODO("Not yet implemented")
    }

    override suspend fun listen() {
        TODO("Not yet implemented")
    }

    override fun publish(topic: String, payload: String, qos: Qos, retain: Boolean) {
        messages += Pair(topic, payload)
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}
