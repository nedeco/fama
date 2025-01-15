package de.osca.fama.mqtt

import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import io.github.davidepianca98.mqtt.broker.interfaces.Authentication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object EnvAuthentication : Authentication, KoinComponent {
    private val settings: Settings by inject()
    @ExperimentalUnsignedTypes
    override fun authenticate(
        clientId: String,
        username: String?,
        password: UByteArray?,
    ): Boolean {
        val envUsername = settings.MQTT_USERNAME
        val envPassword = settings.MQTT_PASSWORD
        if (envUsername != null && envPassword != null) {
            return username == envUsername && password?.toByteArray()?.decodeToString() == envPassword
        }
        return true
    }
}
