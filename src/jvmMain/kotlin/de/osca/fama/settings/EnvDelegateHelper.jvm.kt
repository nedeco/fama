package de.osca.fama.settings

import kotlin.reflect.KProperty

actual fun getEnvString(key: String): String? {
    return System.getenv(key)
}

actual fun KProperty<*>.isMarkedNullable(): Boolean {
    return this.returnType.isMarkedNullable
}
