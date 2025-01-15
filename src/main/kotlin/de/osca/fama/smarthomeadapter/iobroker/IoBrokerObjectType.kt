package de.osca.fama.smarthomeadapter.iobroker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IoBrokerObjectType {
    @SerialName("state") STATE,
    @SerialName("folder") FOLDER
}