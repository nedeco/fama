package de.osca.fama

import co.touchlab.kermit.Logger
import de.osca.fama.settings.EnvVarMissingException
import de.osca.fama.settings.Settings
import io.sentry.Sentry
import io.sentry.kotlin.SentryContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val job =
        GlobalScope.async {
            startKoin { modules(famaModule) }
            object:KoinComponent {
                val famaApplication: FamaApplication by inject()
            }.famaApplication.start()
        }

    runBlocking(SentryContext()) {
        try {
            job.await()
        } catch (e: Throwable) {
            when (val cause = e.cause) {
                is EnvVarMissingException -> {
                    cause.message?.let { Logger.e(it, tag = "ENV") }
                }
                else -> {
                    if (object:KoinComponent {
                            val settings: Settings by inject()
                        }.settings.DEBUG) {
                        Logger.e("Unexpected Error -", e)
                    } else {
                        Logger.e("Unexpected Error - ${e.cause?.message}")
                    }
                    Sentry.captureException(e)
                }
            }
        }
    }
}
