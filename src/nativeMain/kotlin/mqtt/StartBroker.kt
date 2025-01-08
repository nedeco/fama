package mqtt

import io.github.davidepianca98.mqtt.broker.Broker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamaMqttBroker {
    private val broker = Broker(authentication = EnvAuthentication)
    suspend fun start() = withContext(Dispatchers.IO) {
        launch {
            broker.listen()
        }
    }

    fun stop() {
        broker.stop()
    }
}
