package de.osca.fama.settings

import de.osca.fama.famaModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.error.InstanceCreationException
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertFailsWith

class BuildConfigTest : KoinTest {
    private val buildConfig: BuildConfig by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension =
        KoinTestExtension.create {
            modules(
                famaModule,
            )
        }

    @Test
    fun `should throw BuildConfigValueMissingException when buildConfig value is missing`() {
        try {
            buildConfig.sentryDsn
        } catch (e: InstanceCreationException) {
            assertFailsWith<BuildConfigValueMissingException> {
                throw e.cause!!
            }
        }
    }
}
