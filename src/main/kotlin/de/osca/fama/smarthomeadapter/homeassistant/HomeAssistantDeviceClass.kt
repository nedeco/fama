package de.osca.fama.smarthomeadapter.homeassistant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HomeAssistantDeviceClass {
    @SerialName("none")
    NONE,

    @SerialName("apparent_power")
    APPARENT_POWER,

    @SerialName("aqi")
    AQI,

    @SerialName("area")
    AREA,

    @SerialName("atmospheric_pressure")
    ATMOSPHERIC_PRESSURE,

    @SerialName("battery")
    BATTERY,

    @SerialName("blood_glucose_concentration")
    BLOOD_GLUCOSE_CONCENTRATION,

    @SerialName("carbon_dioxide")
    CARBON_DIOXIDE,

    @SerialName("carbon_monoxide")
    CARBON_MONOXIDE,

    @SerialName("current")
    CURRENT,

    @SerialName("data_rate")
    DATA_RATE,

    @SerialName("data_size")
    DATA_SIZE,

    @SerialName("date")
    DATE,

    @SerialName("distance")
    DISTANCE,

    @SerialName("duration")
    DURATION,

    @SerialName("energy")
    ENERGY,

    @SerialName("energy_storage")
    ENERGY_STORAGE,

    @SerialName("enum")
    ENUM,

    @SerialName("frequency")
    FREQUENCY,

    @SerialName("gas")
    GAS,

    @SerialName("humidity")
    HUMIDITY,

    @SerialName("illuminance")
    ILLUMINANCE,

    @SerialName("irradiance")
    IRRADIANCE,

    @SerialName("moisture")
    MOISTURE,

    @SerialName("monetary")
    MONETARY,

    @SerialName("nitrogen_dioxide")
    NITROGEN_DIOXIDE,

    @SerialName("nitrogen_monoxide")
    NITROGEN_MONOXIDE,

    @SerialName("nitrous_oxide")
    NITROUS_OXIDE,

    @SerialName("ozone")
    OZONE,

    @SerialName("ph")
    PH,

    @SerialName("pm1")
    PM1,

    @SerialName("pm25")
    PM25,

    @SerialName("pm10")
    PM10,

    @SerialName("power_factor")
    POWER_FACTOR,

    @SerialName("power")
    POWER,

    @SerialName("precipitation")
    PRECIPITATION,

    @SerialName("precipitation_intensity")
    PRECIPITATION_INTENSITY,

    @SerialName("pressure")
    PRESSURE,

    @SerialName("reactive_power")
    REACTIVE_POWER,

    @SerialName("signal_strength")
    SIGNAL_STRENGTH,

    @SerialName("sound_pressure")
    SOUND_PRESSURE,

    @SerialName("speed")
    SPEED,

    @SerialName("sulphur_dioxide")
    SULPHUR_DIOXIDE,

    @SerialName("temperature")
    TEMPERATURE,

    @SerialName("timestamp")
    TIMESTAMP,

    @SerialName("volatile_organic_compounds")
    VOLATILE_ORGANIC_COMPOUNDS,

    @SerialName("volatile_organic_compounds_parts")
    VOLATILE_ORGANIC_COMPOUNDS_PARTS,

    @SerialName("voltage")
    VOLTAGE,

    @SerialName("volume")
    VOLUME,

    @SerialName("volume_flow_rate")
    VOLUME_FLOW_RATE,

    @SerialName("volume_storage")
    VOLUME_STORAGE,

    @SerialName("water")
    WATER,

    @SerialName("weight")
    WEIGHT,

    @SerialName("wind_speed")
    WIND_SPEED,
}
