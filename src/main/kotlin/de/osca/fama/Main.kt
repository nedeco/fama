package de.osca.fama

import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(famaModule)
    }
    FamaApplication.launch()
}
