package de.osca.fama.mqtt

import io.github.davidepianca98.mqtt.packets.Qos

interface MqttManager {
    fun start()

    fun publish(
        topic: String,
        payload: String,
        qos: Qos = Qos.AT_MOST_ONCE,
        retain: Boolean = false,
    )

    fun stop()

    companion object {
        fun createManager(mqttHost: String?): MqttManager =
            if (mqttHost.isNullOrBlank()) {
                MqttManagerBrokerImpl()
            } else {
                MqttManagerClientImpl(mqttHost)
            }
    }
}
