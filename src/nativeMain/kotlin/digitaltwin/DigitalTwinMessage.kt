package digitaltwin

import kotlinx.serialization.Serializable

/*
@Serializable
data class DigitalTwinMessage(
    val schemaVersion: Int = 1,
    val messageVersion: Int = 1,
    val districtIds: List<String> = emptyList(),
    val eventType: String,
    val eventStartDateTime: Inst? = null,
    val eventEndDateTime: LocalDateTime? = null,
    val expectedDuration: Long? = null,
    val severity: DigitalTwinSeverity = DigitalTwinSeverity.NORMAL,
    val payloadState: DigitalTwinPayloadState = DigitalTwinPayloadState.CREATED,
    val payload: BaseDto,
    val uuid: String = "${eventType}_${payload.id}",
)
*/
