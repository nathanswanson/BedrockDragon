package bedrockDragon.world

import bedrockDragon.network.world.WorldInt2
import bedrockDragon.util.Region
import dev.romainguy.kotlin.math.Float3
import mu.KotlinLogging
import java.util.*

class World {

    private val loadedChunkRelays = Hashtable<WorldInt2, ChunkRelay>()
    private val loadedRegions = Hashtable<WorldInt2, Region>()
    val logger = KotlinLogging.logger {}

    fun generateAt(x: Int, y:Int) {

    }

    fun getOrLoadRelay(absolutePosition: Float3): ChunkRelay {
        logger.info { "chunk relay size: ${loadedChunkRelays.size}" }
       // position.toChunkRelaySpace()
        val relayParentRegion = getOrLoadRegion(absolutePosition)
        //getRelayAt is relative
        //mod position xy by 1024
        //int divide position by 64
        return relayParentRegion.getRelayAt((absolutePosition.x.toInt() shr 10) / 64, (absolutePosition.z.toInt() shr 10) / 64)
    }

    fun getOrLoadRegion(absolutePosition: Float3): Region {
        val intPosition = WorldInt2(absolutePosition.x.toInt() shr 10, absolutePosition.z.toInt() shr 10)


        return if (loadedRegions[intPosition] != null) loadedRegions[intPosition]!! else {
            loadedRegions[intPosition] = Region(intPosition.x, intPosition.y)
            loadedRegions[intPosition]!!
        }
    }



    companion object {
        val tempDefault = World()
    }
}
