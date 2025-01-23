package de.osca.fama.logger

import co.touchlab.kermit.Logger
import de.osca.fama.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LoggerDelegate<in R : Any> :
    ReadOnlyProperty<R, Logger>,
    KoinComponent {
    private val settings: Settings by inject()

    override fun getValue(
        thisRef: R,
        property: KProperty<*>,
    ): Logger = FLogger(thisRef::class.simpleName ?: "Fama", settings.debug)
}

inline fun <reified R : Any> logger() = LoggerDelegate<R>()
