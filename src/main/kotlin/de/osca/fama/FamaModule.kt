package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.mqtt.MqttManagerBrokerImpl
import de.osca.fama.mqtt.MqttManagerClientImpl
import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val famaModule =
    module {
        single<Settings> { SettingsImpl() }
        single<MqttManager> {
            val settings: Settings = get()
            if (settings.mqttHost.isNullOrBlank()) {
                MqttManagerBrokerImpl()
            } else {
                MqttManagerClientImpl(settings.mqttHost!!)
            }
        }
        single<HttpClient> { HttpClient(CIO) }
        single<SmartHomeAdapter> {
            val settings: Settings = get()
            SmartHomeAdapter.getAdapter(settings.smartHomeType)
        }
        single {
            TwinMessageManager()
        }
    }
