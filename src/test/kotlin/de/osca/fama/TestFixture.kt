package de.osca.fama

import de.osca.fama.digitaltwin.model.sensor.Sensor
import de.osca.fama.digitaltwin.model.sensor.SensorStation
import de.osca.fama.digitaltwin.model.sensor.SensorType
import de.osca.fama.digitaltwin.model.sensor.SensorTypeCategory
import kotlinx.datetime.Clock

object TestFixture {
    val sensor =
        Sensor(
            objectId = "sensorId",
            value = 25.0,
            refId = "refId",
            station = SensorStation("stationId", "stationName", Clock.System.now(), Clock.System.now()),
            sensorType =
                SensorType(
                    "sensorTypeId",
                    "sensorTypeName",
                    "definition",
                    SensorTypeCategory.TEMPERATURE,
                    "°C",
                    null,
                    0,
                    Clock.System.now(),
                    Clock.System.now(),
                ),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        )
}
