package de.osca.fama.smarthomeadapter.homeassistant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantDevice(
    @SerialName("ids") val identifiers: String,
    @SerialName("name") val name: String? = null,
    @SerialName("mf") val manufacturer: String? = null,
    @SerialName("mdl") val model: String? = null,
    @SerialName("mdl_id") val modelId: String? = null,
    @SerialName("sn") val serialNumber: String? = null,
    @SerialName("hw") val hwVersion: String? = null,
    @SerialName("sw") val swVersion: String? = null,
    @SerialName("cu") val configurationUrl: String? = null
)
