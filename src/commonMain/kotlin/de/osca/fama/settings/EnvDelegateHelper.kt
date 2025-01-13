package de.osca.fama.settings

import kotlin.reflect.KProperty

expect fun getEnvString(key: String): String?


expect fun KProperty<*>.isMarkedNullable(): Boolean
