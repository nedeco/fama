package de.osca.fama

import co.touchlab.kermit.Logger
import de.osca.fama.settings.EnvVarMissingException

fun main() {
    /*
    if (Settings.ENABLE_SENTRY && BuildConfig.SENTRY_DSN is Nothing) {
        Sentry.init { options ->
            options.dsn = BuildConfig.SENTRY_DSN
        }
    }
     */
    try {
        FamaApplication.start()
    } catch (e: EnvVarMissingException) {
        Logger.e("ENV:", e)
    } catch (e: Throwable) {
        Logger.e("Unexpected Error:", e)
    }
}
