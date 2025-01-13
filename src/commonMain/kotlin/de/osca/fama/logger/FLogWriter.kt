package de.osca.fama.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

object FLogWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        println("$severity: ($tag) $message${throwable?.let { " : $it" } ?: ""}")
    }
}
