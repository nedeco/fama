package de.osca.fama.smarthomeadapter.iobroker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class IoBrokerObjectPayload(
    @SerialName("common") val common: IoBrokerCommon,
    @SerialName("type") val type: IoBrokerObjectType,
) {
    companion object {
        fun from(name: String, unit: String? = null, type: IoBrokerObjectType, commonType: IoBrokerCommonType, icon: String? = null): IoBrokerObjectPayload =
            IoBrokerObjectPayload(
                common = IoBrokerCommon(
                    name = name,
                    unit = unit,
                    type = commonType,
                    icon = icon
                ),
                type = type
            )
    }
}

