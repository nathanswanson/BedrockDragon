package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.world.Chunk

class LevelChunkPacket: PacketPayload() {
    init {
        reliability = Reliability.RELIABLE_ORDERED
    }


    var chunkX = 0
    var chunkZ = 0
    var subChunkCount = 16
    var cacheEnabled = false
    var blobIds = emptyArray<Long>()

    lateinit var data: ByteArray

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

        write(*data)
    }

    companion object {
        fun emptyChunk(x: Int, z: Int): LevelChunkPacket {
            val packet = LevelChunkPacket()

            packet.chunkX = x
            packet.chunkZ = z
            packet.data = Chunk().binary().array()!!

            return packet
        }
    }
}