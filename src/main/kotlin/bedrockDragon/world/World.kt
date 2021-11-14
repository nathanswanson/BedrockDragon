package bedrockDragon.world

import com.curiouscreature.kotlin.math.Float2
import com.curiouscreature.kotlin.math.Float3
import java.util.*

class World {

    private val loadedChunkRelays = Hashtable<Float2, ChunkRelay>()

    fun generateAt(x: Int, y:Int) {

    }

    fun getOrLoadRelay(position: Float2): ChunkRelay {
        position.toChunkRelaySpace()
        return loadedChunkRelays[position] ?: ChunkRelay(position)
    }




}
