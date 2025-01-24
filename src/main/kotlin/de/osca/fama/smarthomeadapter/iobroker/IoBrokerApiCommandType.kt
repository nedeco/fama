package de.osca.fama.smarthomeadapter.iobroker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IoBrokerApiCommandType {
    @SerialName("object")
    OBJECT,

    @SerialName("state")
    STATE,
}
