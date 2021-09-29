package bedrockDragon

import bedrockDragon.ticking.EntityTicker
import bedrockDragon.ticking.WorldTicker
import com.nukkitx.protocol.bedrock.BedrockServer
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}

class Server(bindAddress: InetSocketAddress?) : BedrockServer(bindAddress) {

    private var isRunning = false
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    fun start() {
        //Coroutine Entity
        scope.launch { entityLightThread() }
        //Coroutine World
        scope.launch { worldLightThread() }
        //Start server tick.
        //Main thread deals with packets received and sent to client. packets received are converted into objects and sent to the related lightThread
        isRunning = true
        tick()
    }

    private fun tick() {
        //var lastTick = System.currentTimeMillis()
        while(isRunning) {
            //Run 20 ticks a second
            //1 second = 1000ms
            //1000ms/20 = 50
            //if(System.currentTimeMillis() - lastTick >= 50) {
            //    logger.info { "tick ${System.currentTimeMillis()}" }
           //     lastTick = System.currentTimeMillis()
           // }
        }
    }

    fun stop() {
        isRunning = false
    }

    private fun entityLightThread() {
        val entityTicker = EntityTicker()
        entityTicker.initialize()
    }

    private fun worldLightThread() {
        val worldTicker = WorldTicker()
        worldTicker.initialize()
    }
}