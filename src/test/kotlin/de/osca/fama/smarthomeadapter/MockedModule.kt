package de.osca.fama.smarthomeadapter

import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.BuildConfig
import de.osca.fama.settings.Settings
import de.osca.fama.smarthomeadapter.homeassistant.HomeAssistantMockMqttManager
import de.osca.fama.smarthomeadapter.iobroker.IoBrokerMockEngineInterceptor
import io.ktor.client.HttpClient
import org.koin.dsl.module

val mockModules =
    module {
        single<BuildConfig> { MockedBuildConfig() }
        single<Settings> { MockedSettings() }
        single<HomeAssistantMockMqttManager> { HomeAssistantMockMqttManager() }
        single<IoBrokerMockEngineInterceptor> { IoBrokerMockEngineInterceptor() }
        single<MqttManager> { get<HomeAssistantMockMqttManager>() }
        single<HttpClient> {
            val mockEngineInterceptor: IoBrokerMockEngineInterceptor = get()
            HttpClient(mockEngineInterceptor.mockEngine)
        }
    }
