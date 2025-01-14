package de.osca.fama.digitaltwin.model.sensor

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SensorStation(
    val objectId: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
