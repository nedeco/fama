package de.osca.fama.settings

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import kotlin.reflect.KProperty

@OptIn(ExperimentalForeignApi::class)
actual fun getEnvString(key: String): String? {
    return getenv(key)?.toKString()
}

actual fun KProperty<*>.isMarkedNullable(): Boolean {
    return this.returnType.isMarkedNullable
}
