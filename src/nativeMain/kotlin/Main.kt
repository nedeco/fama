import digitaltwin.startTwinConnection
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import mqtt.FamaMqttBroker
import platform.posix.SIGINT
import platform.posix.exit
import platform.posix.signal
import smartHomeAdapter.HomeAssistantAdapter


@OptIn(ExperimentalForeignApi::class)
suspend fun main() {
    val famaMqttBroker = FamaMqttBroker()
    signal(SIGINT, staticCFunction<Int, Unit> {
        println("Interrupt: $it")
        famaMqttBroker.stop()
        exit(0)
    })
    startTwinConnection()
    famaMqttBroker.start()
    val ha = HomeAssistantAdapter()
    ha.createSensorStation()
}
