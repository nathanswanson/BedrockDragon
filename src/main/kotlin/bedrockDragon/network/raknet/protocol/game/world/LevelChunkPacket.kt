package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.world.chunk.Chunk
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream

class LevelChunkPacket(chunk: Chunk): PacketPayload(MinecraftPacketConstants.LEVEL_CHUNK) {

    var data: ByteArray = chunk.payload ?:
        throw NullPointerException("LevelChunkPacket should be called only with a complete chunk but chunk.payload: ${chunk.payload}")

    var chunkX = chunk.position.x
    var chunkZ = chunk.position.y
    var subChunkCount = chunk.readyNonEmptySectionCount()
    var cacheEnabled = false
    var blobIds = emptyArray<Long>()


    override fun encode() {
        if(buffer().readerIndex() != 0)
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

    private fun write(fastByteArrayOutputStream: FastByteArrayOutputStream) {
        for(i in 0 until fastByteArrayOutputStream.length) {
            write(fastByteArrayOutputStream.array[i])
        }
    }
}