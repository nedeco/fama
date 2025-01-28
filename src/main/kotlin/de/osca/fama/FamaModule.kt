package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.generated.BuildConfigImpl
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.BuildConfig
import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import org.koin.dsl.module

val famaModule =
    module {
        single<BuildConfig> {
            BuildConfigImpl()
        }
        single<Settings> {
            SettingsImpl()
        }
        single<MqttManager> {
            val settings: Settings = get()
            MqttManager.createManager(settings.mqttHost)
        }
        single<HttpClient> {
            HttpClient(CIO) {
                install(WebSockets)
            }
        }
        single<SmartHomeAdapter> {
            val settings: Settings = get()
            SmartHomeAdapter.createAdapter(settings.smartHomeType)
        }
        single<TwinMessageManager> {
            TwinMessageManager()
        }
    }
