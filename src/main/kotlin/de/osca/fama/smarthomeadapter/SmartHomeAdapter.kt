package de.osca.fama.smarthomeadapter

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.mqtt.MqttManager
import org.koin.core.component.KoinComponent

interface SmartHomeAdapter {
    suspend fun updateSensorStation(sensor: Sensor)

    enum class Type {
        HA,
        IB,
    }

    companion object {
        fun getAdapter(type: Type): SmartHomeAdapter = when (type) {
            Type.HA -> HomeAssistantAdapter()
            Type.IB -> IoBrokerAdapter()
        }
    }
}
