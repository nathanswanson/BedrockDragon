package bedrockDragon.world

import bedrockDragon.network.world.WorldInt2
import mu.KotlinLogging
import java.util.*

class World {

    private val loadedChunkRelays = Hashtable<WorldInt2, ChunkRelay>()
    val logger = KotlinLogging.logger {}

    fun generateAt(x: Int, y:Int) {

    }

    fun getOrLoadRelay(position: WorldInt2): ChunkRelay {
        //logger.info { "chunk relay size: ${loadedChunkRelays.size}" }
       // position.toChunkRelaySpace()


        return if (loadedChunkRelays[position] != null) loadedChunkRelays[position]!! else {
           // loadedChunkRelays[position] = ChunkRelay(position)
            loadedChunkRelays[position]!!
        }

    }



    companion object {
        val tempDefault = World()
    }
}
