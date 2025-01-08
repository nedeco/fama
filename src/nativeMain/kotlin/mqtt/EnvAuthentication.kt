package mqtt

import io.github.davidepianca98.mqtt.broker.interfaces.Authentication
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

object EnvAuthentication: Authentication {
    @OptIn(ExperimentalForeignApi::class)
    override fun authenticate(clientId: String, username: String?, password: UByteArray?): Boolean {
        val envUsername = getenv("username")?.toKString()
        val envPassword = getenv("password")?.toKString()
        if (envUsername != null && envPassword != null) {
            return username == envUsername && password?.toByteArray()?.decodeToString() == envPassword
        }
        return true
    }
}
