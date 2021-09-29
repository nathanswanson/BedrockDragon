package bedrockDragon.ticking

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class WorldTicker : TickingThread {
    override fun tick() {
        var lastTick = System.currentTimeMillis()
        while(true) {
            if(System.currentTimeMillis() - lastTick >= 50) {
                logger.info { "world tick ${System.currentTimeMillis()}" }
                lastTick = System.currentTimeMillis()
            }
        }
    }

    override fun initialize() {
        tick()
    }
}