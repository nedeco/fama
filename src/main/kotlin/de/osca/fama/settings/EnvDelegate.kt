package de.osca.fama.settings

import kotlin.reflect.KProperty

class EnvDelegate<T>(
    private val key: String,
    private val defaultValue: T? = null,
    private val validator: ((T?) -> Boolean)? = null,
    private val validationError: String,
    private val converter: (String) -> T?,
) {
    private val value: T? by lazy {
        val rawValue = System.getenv(key)

        val convertedValue =
            if (rawValue?.isBlank() == true) {
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

    @Throws(EnvVarMissingException::class)
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = value ?: if (property.returnType.isMarkedNullable) {
        @Suppress("UNCHECKED_CAST")
        value as T
    } else {
        throw EnvVarMissingException(property.name) // throw if not nullable
    }
}

private fun Boolean?.orDefault(default: Boolean) = this ?: default

@Throws(EnvVarMissingException::class)
inline fun <reified T> env(
    key: String,
    defaultValue: T? = null,
    noinline validator: ((T?) -> Boolean)? = null,
    validationError: String = "Validation failed for variable '$key'.",
    noinline converter: (String) -> T?,
): EnvDelegate<T> = EnvDelegate(key, defaultValue, validator, validationError, converter)

@Throws(EnvVarMissingException::class)
fun envString(
    key: String,
    defaultValue: String? = null,
): EnvDelegate<String> = env(key, defaultValue, converter = { it })

@Throws(EnvVarMissingException::class)
fun envInt(
    key: String,
    defaultValue: Int? = null,
): EnvDelegate<Int> = env(key, defaultValue, converter = { it.toIntOrNull() })

@Throws(EnvVarMissingException::class)
fun envBoolean(
    key: String,
    defaultValue: Boolean = false,
): EnvDelegate<Boolean> = env(key, defaultValue, converter = { it.toBooleanStrictOrNull() })

@Throws(EnvVarMissingException::class)
inline fun <reified T : Enum<T>> envEnum(
    key: String,
    defaultValue: T? = null,
): EnvDelegate<T> = env(key, defaultValue, converter = { value ->
    enumValues<T>().find { it.name.equals(value, ignoreCase = true) }
})
