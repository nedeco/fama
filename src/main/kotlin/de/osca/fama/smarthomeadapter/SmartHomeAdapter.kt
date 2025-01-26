package de.osca.fama.smarthomeadapter

import de.osca.fama.digitaltwin.model.sensor.Sensor

interface SmartHomeAdapter {
    val mqttEnabled: Boolean get() = true

    suspend fun updateSensorStation(sensor: Sensor)

    enum class Type {
        HA,
        IB,
    }

    companion object {
        fun createAdapter(type: Type): SmartHomeAdapter = when (type) {
            Type.HA -> HomeAssistantAdapter()
            Type.IB -> IoBrokerAdapter()
        }
    }
}
