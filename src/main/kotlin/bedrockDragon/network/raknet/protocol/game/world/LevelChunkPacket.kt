package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.world.chunk.Chunk
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream

class LevelChunkPacket(chunk: Chunk): PacketPayload(MinecraftPacketConstants.LEVEL_CHUNK) {
    init {
        chunk.initChunkFromStorage()
        reliability = Reliability.RELIABLE_ORDERED
    }


    var chunkX = chunk.position.x
    var chunkZ = chunk.position.y
    var subChunkCount = chunk.sectionCount()
    var cacheEnabled = false
    var blobIds = emptyArray<Long>()

    var data: FastByteArrayOutputStream = chunk.encodePayload()

    override fun encode() {
        writeVarInt(chunkX)
        writeVarInt(chunkZ)
        writeUnsignedVarInt(1) //subchunk size
        writeBoolean(cacheEnabled)
        if(cacheEnabled) {
            writeUnsignedVarInt(blobIds.size)
            for(id in blobIds) {
                writeLongLE(id)
            }
        }
        data.use {
            writeUnsignedVarInt(it.length)
            write(it)
        }

    }

    private fun write(fastByteArrayOutputStream: FastByteArrayOutputStream) {
        for(i in 0 until fastByteArrayOutputStream.length) {
            write(fastByteArrayOutputStream.array[i])
        }
    }
}