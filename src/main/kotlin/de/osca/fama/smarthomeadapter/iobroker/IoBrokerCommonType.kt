package de.osca.fama.smarthomeadapter.iobroker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IoBrokerCommonType {
    @SerialName("folder") FOLDER,
    @SerialName("number") NUMBER
}