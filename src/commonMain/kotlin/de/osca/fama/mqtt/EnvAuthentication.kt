package de.osca.fama.mqtt

import de.osca.fama.settings.Settings
import io.github.davidepianca98.mqtt.broker.interfaces.Authentication

object EnvAuthentication: Authentication {
    @ExperimentalUnsignedTypes
    override fun authenticate(clientId: String, username: String?, password: UByteArray?): Boolean {
        val envUsername = Settings.MQTT_USERNAME
        val envPassword = Settings.MQTT_PASSWORD
        if (envUsername != null && envPassword != null) {
            return username == envUsername && password?.toByteArray()?.decodeToString() == envPassword
        }
        return true
    }
}
