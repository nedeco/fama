package de.osca.fama.smarthomeadapter.homeassistant

import de.osca.fama.mqtt.MqttManager
import io.github.davidepianca98.mqtt.packets.Qos

class HomeAssistantMockMqttManager : MqttManager {
    private var started = false
    var messages = mutableListOf<Pair<String, String>>()

    override fun start() {
        started = true
    }

    override fun publish(
        topic: String,
        payload: String,
        qos: Qos,
        retain: Boolean,
    ) {
        messages += Pair(topic, payload)
    }

    override fun stop() {
        started = false
    }
}
