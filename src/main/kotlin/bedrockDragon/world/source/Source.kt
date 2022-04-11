package bedrockDragon.world.source

import bedrockDragon.util.WorldInt2
import bedrockDragon.world.World
import bedrockDragon.world.chunk.Chunk
import bedrockDragon.world.chunk.ChunkRelay

abstract class Source(val world: World) {
    var relayGrid = HashMap<WorldInt2, ChunkRelay>()


    /**
     * [getRelayAt] uses relative position from region to find a relay. If one does not exist is creates it.
     *
     */
    fun getRelayAt(x: Int, z: Int): ChunkRelay {
        val worldPos = WorldInt2(x,z)
        val nRelay = relayGrid[worldPos]

        if(nRelay == null)
            relayGrid[worldPos] = ChunkRelay(worldPos, world)
        return relayGrid[worldPos]!!
    }

    fun getChunkAtIdx(x: Int, z: Int): Chunk {
        return getRelayAt(x shr 2, z shr 2).getChunk2D(
            x.mod(4),
            z.mod(4)
        )
    }

    abstract fun writeChunkBinary(chunk: Chunk)
    abstract fun readChunkBinary(chunk: Chunk): ByteArray
}