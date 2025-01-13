package de.osca.fama

import co.touchlab.kermit.Logger

fun main() {
    /*
    if (Settings.ENABLE_SENTRY && BuildConfig.SENTRY_DSN is Nothing) {
        Sentry.init { options ->
            options.dsn = BuildConfig.SENTRY_DSN
        }
    }
    */
    FamaApplication.start()
}
