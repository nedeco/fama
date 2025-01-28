package de.osca.fama.digitaltwin

import de.osca.fama.famaModule
import de.osca.fama.settings.BuildConfig
import de.osca.fama.smarthomeadapter.SmartHomeAdapter
import de.osca.fama.smarthomeadapter.mockModules
import io.ktor.client.HttpClient
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkConstructor
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@ExtendWith(MockKExtension::class)
class TwinMessageManagerTest : KoinTest {
    private val twinMessageManager: TwinMessageManager by inject()

    @RelaxedMockK
    private lateinit var smartHomeAdapter: SmartHomeAdapter

    @RelaxedMockK
    private lateinit var httpClient: HttpClient

    @RelaxedMockK
    private lateinit var buildConfig: BuildConfig

    @RelaxedMockK
    private lateinit var stompSession: StompSession

    @JvmField
    @RegisterExtension
    val koinTestExtension =
        KoinTestExtension.create {
            modules(
                famaModule,
                mockModules,
                module {
                    single {
                        smartHomeAdapter
                    }
                    single {
                        httpClient
                    }
                    single {
                        buildConfig
                    }
                },
            )
        }

    @BeforeEach
    fun setUp() {
        every { buildConfig.rabbitmqStompUrl } returns "ws://localhost:15674/ws"
        every { buildConfig.rabbitmqStompUsername } returns "guest"
        every { buildConfig.rabbitmqStompPassword } returns "guest"
        mockkConstructor(StompClient::class)
        coEvery { anyConstructed<StompClient>().connect(any(), any(), any(), any()) } returns stompSession
    }

    @Test
    fun `test start method`() = runTest {
        twinMessageManager.start()
        verify(atLeast = 1) { buildConfig.rabbitmqStompUrl }
        verify { httpClient wasNot Called }
        coVerify { anyConstructed<StompClient>().connect(allAny()) }
    }

    @Test
    fun `test stop method`() = runTest {
        twinMessageManager.start()
        twinMessageManager.stop()
        verify(atLeast = 1) { buildConfig.rabbitmqStompUrl }
        verify { smartHomeAdapter wasNot Called }
        coVerify { stompSession.disconnect() }
    }

    /*@Test
    fun `test listenSensors method`() = runTest {
        val stompSubscribeHeaders =
            spyk<StompSubscribeHeaders>(
                "",
                StompSubscribeHeaders::class,
            )

        coEvery {
            stompSession.subscribe(
                stompSubscribeHeaders,
            )
        } returns
            flowOf(
                StompFrame.Message(
                    StompMessageHeaders(
                        destination = "/amq/queue/s.public.sensor",
                        messageId = "test",
                        subscription = "test",
                    ) {
                        ack = AckMode.CLIENT.headerValue
                    },
                    FrameBody.Text(
                        json.encodeToString(DigitalTwinMessage(uuid = "test", eventType = "sensor", payload = TestFixture.sensor)),
                    ),
                ),
            )
        twinMessageManager.start()
        twinMessageManager.listenSensors()
        delay(2000)
        coVerify { smartHomeAdapter.updateSensorStation(any()) }
        coVerify { stompSession.subscribe(allAny()) }
    }
     */

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val json =
            Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
                explicitNulls = false
            }
    }
}
