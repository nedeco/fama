package de.osca.fama.smarthomeadapter.homeassistant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantOrigin(
    @SerialName("name") val name: String? = null,
    @SerialName("sw") val swVersion: String? = null,
    @SerialName("url") val supportUrl: String? = null
)
