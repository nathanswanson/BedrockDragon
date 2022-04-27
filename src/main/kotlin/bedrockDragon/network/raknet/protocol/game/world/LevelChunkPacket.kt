package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.world.chunk.Chunk

class LevelChunkPacket(chunk: Chunk, payload: ByteArray): PacketPayload(MinecraftPacketConstants.LEVEL_CHUNK) {



    var data: ByteArray = payload// ?:
        //throw NullPointerException("LevelChunkPacket should be called only with a complete chunk but chunk.payload: ${chunk.payload}")

    var chunkX = chunk.position.x
    var chunkZ = chunk.position.y
    var subChunkCount = chunk.readyNonEmptySectionCount()
    var cacheEnabled = false
    var blobIds = emptyArray<Long>()


    override suspend fun encode() {
        if(buffer().readerIndex() > 0)
            return
        writeVarInt(chunkX)
        writeVarInt(chunkZ)
        writeUnsignedVarInt(subChunkCount) //subchunk size
        writeBoolean(cacheEnabled)
        if(cacheEnabled) {
            writeUnsignedVarInt(blobIds.size)
            for(id in blobIds) {
                writeLongLE(id)
            }
        }
        writeUnsignedVarInt(data.size)
        write(*data)


    }
}