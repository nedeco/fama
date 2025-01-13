package de.osca.fama.smarthomeadapter

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.mqtt.MqttManager

interface SmartHomeAdapter {
    val mqtt: MqttManager
        get() = MqttManager

    suspend fun updateSensorStation(sensor: Sensor)

    enum class Type {
        HA,
        OH,
        IB
    }

    companion object {
        fun getAdapter(type: Type): SmartHomeAdapter {
            return when (type) {
                Type.HA -> HomeAssistantAdapter()
                Type.OH -> TODO()
                Type.IB -> TODO()
            }
        }
    }
}
