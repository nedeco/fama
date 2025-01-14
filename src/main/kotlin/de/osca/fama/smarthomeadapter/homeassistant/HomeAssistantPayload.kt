package de.osca.fama.smarthomeadapter.homeassistant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantPayload(
    @SerialName("uniq_id") val uniqueId: String,
    @SerialName("stat_t") val stateTopic: String,
    @SerialName("name") val name: String? = null,
    @SerialName("ic") val icon: String? = null,
    @SerialName("unit_of_meas") val unitOfMeasurement: String? = null,
    @SerialName("val_tpl") val valueTemplate: String? = null,
    @SerialName("dev_cla") val deviceClass: HomeAssistantDeviceClass? = null,
    @SerialName("dev") val device: HomeAssistantDevice,
    @SerialName("o") val origin: HomeAssistantOrigin,
)
