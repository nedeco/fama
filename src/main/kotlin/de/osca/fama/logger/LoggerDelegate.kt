package de.osca.fama.logger

import co.touchlab.kermit.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
    override fun getValue(
        thisRef: R,
        property: KProperty<*>,
    ): Logger = FLogger(thisRef::class.simpleName ?: "Fama")
}

inline fun <reified R : Any> logger() = LoggerDelegate<R>()
