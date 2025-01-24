package de.osca.fama.logger

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter

class FLogger(
    tag: String,
    debug: Boolean,
) : Logger(
        config =
            loggerConfigInit(
                platformLogWriter(),
                minSeverity =
                    if (debug) {
                        Severity.Debug
                    } else {
                        Severity.Info
                    },
            ),
        tag = tag,
    )
