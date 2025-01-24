package de.osca.fama.digitaltwin.model.sensor

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SensorTypeCategorySerializer::class)
enum class SensorTypeCategory {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    NONE,
    APPARENT_POWER,
    AQI,
    AREA,
    ATMOSPHERIC_PRESSURE,
    BATTERY,
    BLOOD_GLUCOSE_CONCENTRATION,
    CARBON_DIOXIDE,
    CARBON_MONOXIDE,
    CURRENT,
    DATA_RATE,
    DATA_SIZE,
    DATE,
    DISTANCE,
    DURATION,
    ENERGY,
    ENERGY_STORAGE,
    ENUM,
    FREQUENCY,
    FRICTION_COEFFICIENT,
    GAS,
    HUMIDITY,
    ILLUMINANCE,
    IRRADIANCE,
    MOISTURE,
    MONETARY,
    NITROGEN_DIOXIDE,
    NITROGEN_MONOXIDE,
    NITROUS_OXIDE,
    OZONE,
    PH,
    PM1,
    PM25,
    PM10,
    POWER_FACTOR,
    POWER,
    PRECIPITATION,
    PRECIPITATION_INTENSITY,
    PRESSURE,
    REACTIVE_POWER,
    ROAD_CONDITION,
    SIGNAL_STRENGTH,
    SOUND_PRESSURE,
    SPEED,
    SULPHUR_DIOXIDE,
    SNOW_HEIGHT,
    SALINITY,
    TEMPERATURE,
    TIMESTAMP,
    UV_INDEX,
    VOLATILE_ORGANIC_COMPOUNDS,
    VOLATILE_ORGANIC_COMPOUNDS_PARTS,
    VOLTAGE,
    VOLUME,
    VOLUME_FLOW_RATE,
    VOLUME_STORAGE,
    WATER,
    WATER_FILM_HEIGHT,
    WEIGHT,
    WIND_SPEED,
    WIND_DIRECTION,
}

object SensorTypeCategorySerializer : KSerializer<SensorTypeCategory> {
    override val descriptor: SerialDescriptor = serialDescriptor<SensorTypeCategory>()

    override fun deserialize(decoder: Decoder): SensorTypeCategory {
        return try {
            SensorTypeCategory.valueOf(decoder.decodeString())
        } catch (e: IllegalArgumentException) {
            SensorTypeCategory.NONE // Default value when none matches
        }
    }

    override fun serialize(encoder: Encoder, value: SensorTypeCategory) {
        encoder.encodeString(value.name)
    }
}
