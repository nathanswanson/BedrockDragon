package bedrockDragon.ticking

import mu.KotlinLogging

class EntityTicker : TickingThread {
    private val logger = KotlinLogging.logger {}

    override fun tick() {
        var lastTick = System.currentTimeMillis()
        while(true) {
            if(System.currentTimeMillis() - lastTick >= 50) {
                    //logger.info { "entity tick ${System.currentTimeMillis()}" }
                    lastTick = System.currentTimeMillis()
             }
        }
    }

    override fun initialize() {
        tick()
    }
}