package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.generated.BuildConfigImpl
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.mqtt.MqttManagerBrokerImpl
import de.osca.fama.mqtt.MqttManagerClientImpl
import de.osca.fama.settings.BuildConfig
import de.osca.fama.settings.Settings
import de.osca.fama.settings.SettingsImpl
import de.osca.fama.smarthomeadapter.HomeAssistantAdapter
import de.osca.fama.smarthomeadapter.IoBrokerAdapter
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify
import kotlin.reflect.KClass
import kotlin.test.assertIs

@ExtendWith(MockKExtension::class)
class FamaModuleCheck : KoinTest {
    @RelaxedMockK
    private lateinit var settings: Settings

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check koin module`() {
        famaModule.verify(
            injections =
                injectedParameters(
                    definition<BuildConfig>(BuildConfigImpl::class),
                    definition<MqttManager>(MqttManagerBrokerImpl::class),
                    definition<Settings>(SettingsImpl::class),
                    definition<HttpClient>(HttpClientEngine::class, HttpClientConfig::class),
                    definition<TwinMessageManager>(TwinMessageManager::class),
                ),
        )
    }

    @Test
    fun `check koin module MqttManagerImpl`() {
        every { settings.mqttHost } returns "mqtt://localhost"
        val module =
            famaModule
                .apply {
                    single<Settings> {
                        settings
                    }
                }

        startKoin {
            modules(module)
        }

        val mqttManagerClient by inject<MqttManager>()

        assertIs<MqttManagerClientImpl>(mqttManagerClient)
        stopKoin()

        every { settings.mqttHost } returns null

        startKoin {
            modules(module)
        }

        val mqttManagerBroker by inject<MqttManager>()

        assertIs<MqttManagerBrokerImpl>(mqttManagerBroker)

        stopKoin()
    }

    private val smartHomeAdapterMap =
        mapOf<SmartHomeAdapter.Type, KClass<*>>(
            Pair(SmartHomeAdapter.Type.HA, IoBrokerAdapter::class),
            Pair(SmartHomeAdapter.Type.HA, HomeAssistantAdapter::class),
        )

    @Test
    fun `check koin module SmartHomeAdapter Inject IB`() {
        testInjectAdapter<IoBrokerAdapter>(SmartHomeAdapter.Type.IB)
    }

    @Test
    fun `check koin module SmartHomeAdapter Inject HA`() {
        testInjectAdapter<HomeAssistantAdapter>(SmartHomeAdapter.Type.HA)
    }

    private inline fun <reified T> testInjectAdapter(type: SmartHomeAdapter.Type) {
        every { settings.smartHomeType } returns type

        val module =
            famaModule
                .apply {
                    single<Settings> {
                        settings
                    }
                }
        startKoin {
            modules(module)
        }
        val smartHomeAdapter by inject<SmartHomeAdapter>()
        assertIs<T>(smartHomeAdapter)

        stopKoin()
    }
}
