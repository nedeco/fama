package de.osca.fama

import de.osca.fama.digitaltwin.TwinMessageManager
import de.osca.fama.mqtt.MqttManager
import de.osca.fama.settings.BuildConfig
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class FamaApplicationTest : KoinTest {
    @RelaxedMockK
    private lateinit var smartHomeAdapter: SmartHomeAdapter

    @RelaxedMockK
    private lateinit var buildConfig: BuildConfig

    @RelaxedMockK
    private lateinit var twinMessageManager: TwinMessageManager

    @RelaxedMockK
    private lateinit var mqttManager: MqttManager

    @JvmField
    @RegisterExtension
    val koinTestExtension =
        KoinTestExtension.create {
            modules(
                mockModules,
                module {
                    single {
                        smartHomeAdapter
                    }
                    single {
                        buildConfig
                    }
                    single {
                        twinMessageManager
                    }
                    single {
                        mqttManager
                    }
                },
            )
        }

    @BeforeEach
    fun setUp() {
        every { buildConfig.sentryDsn } returns null
        every { smartHomeAdapter.mqttEnabled } returns true
    }

    @Test
    fun `test launch method`() =
        runTest {
            coEvery { twinMessageManager.start() } just Runs
            coEvery {
                twinMessageManager.listenSensors()
                Unit
            } just Runs
            coEvery { twinMessageManager.stop() } just Runs
            coEvery { mqttManager.start() } just Runs
            coEvery { mqttManager.stop() } just Runs

            FamaApplication.launch()

            coVerify { twinMessageManager.start() }
            coVerify { twinMessageManager.listenSensors() }
            coVerify { twinMessageManager.stop() }
            coVerify { mqttManager.start() }
            coVerify { mqttManager.stop() }
        }
}
