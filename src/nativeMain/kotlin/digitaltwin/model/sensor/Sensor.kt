package digitaltwin.model.sensor

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Sensor(
    val objectId: String,
    val value: Int,
    val refId: String,
    val station: SensorStation,
    val sensorType: SensorType,
    val createdAt: Instant,
    val updatedAt: Instant
)
