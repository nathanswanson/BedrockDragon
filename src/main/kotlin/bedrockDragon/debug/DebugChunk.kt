package bedrockDragon.debug

import bedrockDragon.network.raknet.Packet

class DebugChunk: Packet() {
    init {
        writeInt(0) //chunkx
        writeInt(0) //chunky

        writeVarInt(0) //entites length
        //block entities

    }

    fun writeSubChunk() {

    }
}