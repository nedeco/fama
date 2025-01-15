package de.osca.fama.smarthomeadapter

import de.osca.fama.mqtt.MqttManager
import de.osca.fama.mqtt.MqttManagerImpl
import de.osca.fama.settings.Settings
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import org.koin.dsl.module

fun getMockedFamaModule(httpClient: HttpClient? = null) = module {
    single<Settings> { MockedSettings() }
    single<MqttManager> { MqttManagerImpl() }
    single<HttpClient> { httpClient ?: HttpClient(MockEngine) }
}
