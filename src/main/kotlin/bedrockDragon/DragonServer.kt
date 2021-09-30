package bedrockDragon

import bedrockDragon.ticking.EntityTicker
import bedrockDragon.ticking.WorldTicker
import kotlinx.coroutines.*
import mu.KotlinLogging
import network.common.util.EventLoops
import protocol.bedrock.Bedrock
import protocol.bedrock.BedrockServerEventHandler
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}

class DragonServer(bindAddress: InetSocketAddress) : Bedrock(EventLoops.commonGroup()) {

    private var isRunning = false
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    lateinit var handler : BedrockServerEventHandler

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

    override fun onTick() {
        TODO("Not yet implemented")
    }

    override fun close(force: Boolean) {
        TODO("Not yet implemented")
    }

}