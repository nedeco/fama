package de.osca.fama.settings

import kotlin.reflect.KProperty

class EnvDelegate<T>(
    private val key: String,
    private val defaultValue: T? = null,
    private val validator: ((T?) -> Boolean)? = null,
    private val validationError: String,
    private val converter: (String) -> T?
) {
    private val value: T? by lazy {
        val rawValue = getEnvString(key)

        val convertedValue =  if (rawValue?.isBlank() == true) {
            defaultValue
        } else {
            rawValue?.let { converter(it) } ?: defaultValue
        }

        if (convertedValue == null && defaultValue == null && !validator?.invoke(null).orDefault(true)) {
            throw EnvVarMissingException(key)
        }
        if (convertedValue != null && validator != null && !validator.invoke(convertedValue)) {
            throw IllegalArgumentException(validationError)
        }
        convertedValue
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: if (property.isMarkedNullable()) {
            @Suppress("UNCHECKED_CAST")
            value as T
        } else {
            throw EnvVarMissingException(property.name) // throw if not nullable
        }
    }
}

private fun Boolean?.orDefault(default: Boolean) = this ?: default

inline fun <reified T> env(
    key: String,
    defaultValue: T? = null,
    noinline validator: ((T?) -> Boolean)? = null,
    validationError: String = "Validation failed for variable '$key'.",
    noinline converter: (String) -> T?
): EnvDelegate<T> =
    EnvDelegate(key, defaultValue, validator, validationError, converter)

fun envString(key: String, defaultValue: String? = null): EnvDelegate<String> =
    env(key, defaultValue, converter = { it })

fun envInt(key: String, defaultValue: Int? = null): EnvDelegate<Int> =
    env(key, defaultValue, converter = { it.toIntOrNull() })

fun envBoolean(key: String, defaultValue: Boolean = false): EnvDelegate<Boolean> =
    env(key, defaultValue, converter = { it.toBooleanStrictOrNull() })

inline fun <reified T : Enum<T>> envEnum(
    key: String,
    defaultValue: T? = null,
): EnvDelegate<T> =
    env(key, defaultValue, converter = { value ->
        enumValues<T>().find { it.name.equals(value, ignoreCase = true) }
    })
