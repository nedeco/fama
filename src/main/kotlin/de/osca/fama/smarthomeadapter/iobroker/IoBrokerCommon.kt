package de.osca.fama.smarthomeadapter.iobroker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IoBrokerCommon(
    @SerialName("name") var name: String,
    @SerialName("unit") var unit: String?,
    @SerialName("type") val type: IoBrokerCommonType,
    @SerialName("icon") val icon: String?,
)
