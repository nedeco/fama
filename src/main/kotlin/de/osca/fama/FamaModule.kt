package de.osca.fama

import de.osca.fama.mqtt.MqttManager
import de.osca.fama.mqtt.MqttManagerImpl
import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.koin.dsl.module

val famaModule = module {
    single<Settings> { SettingsImpl() }
    single<MqttManager> { MqttManagerImpl() }
    single<HttpClient> { HttpClient(CIO) }
    single<FamaApplication> { FamaApplication() }
}
