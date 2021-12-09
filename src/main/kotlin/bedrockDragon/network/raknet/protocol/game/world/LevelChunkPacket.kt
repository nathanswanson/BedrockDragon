package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.world.Chunk
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import java.io.OutputStream

class LevelChunkPacket(chunk: Chunk): PacketPayload(MinecraftPacketConstants.LEVEL_CHUNK) {
    init {
        chunk.loadFromNbt()
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
        writeUnsignedVarInt(subChunkCount)
        writeBoolean(cacheEnabled)
        if(cacheEnabled) {
            writeUnsignedVarInt(blobIds.size)
            for(id in blobIds) {
                writeLongLE(id)
            }
        }
        data.use {
            writeUnsignedVarInt(it.length)
            write(*it.array)
        }

    }
}