package bedrockDragon

import com.nukkitx.protocol.bedrock.BedrockServer
import kotlin.coroutines.*
import mu.KotlinLogging
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}

class Server(bindAddress: InetSocketAddress?) : BedrockServer(bindAddress) {

    private var isRunning = false

    fun start() {
        //Coroutine Entity
        //Coroutine World

        //Start server tick.
        isRunning = true
        tick()
    }

    private fun tick() {
        var lastTick = System.currentTimeMillis()
        while(isRunning) {
            //Run 20 ticks a second
            //1 second = 1000ms
            //1000ms/20 = 50
            if(System.currentTimeMillis() - lastTick >= 50) {
                logger.info { "tick ${System.currentTimeMillis()}" }
                lastTick = System.currentTimeMillis()
            }
        }
    }

    fun stop() {
        isRunning = false
    }

    suspend fun entityLightThread() {

    }

    suspend fun worldLightThread() {

    }
}