package de.osca.fama.smarthomeadapter.iobroker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IoBrokerStatePayload(
    @SerialName("val") var value: Double,
    @SerialName("ack") var ack: Boolean = true,
)
