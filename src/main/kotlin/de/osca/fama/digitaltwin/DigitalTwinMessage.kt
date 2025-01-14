package de.osca.fama.digitaltwin

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class DigitalTwinMessage<T>(
    val schemaVersion: Int = 1,
    val messageVersion: Int = 1,
    val districtIds: List<String> = emptyList(),
    val eventType: String,
    val eventStartDateTime: Instant? = null,
    val eventEndDateTime: Instant? = null,
    val expectedDuration: Long? = null,
    val severity: DigitalTwinSeverity = DigitalTwinSeverity.NORMAL,
    val payloadState: DigitalTwinPayloadState = DigitalTwinPayloadState.CREATED,
    val payload: T,
    val uuid: String,
)
