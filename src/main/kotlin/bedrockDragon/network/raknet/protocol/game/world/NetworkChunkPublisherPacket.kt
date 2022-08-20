package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class NetworkChunkPublisherPacket: PacketPayload(MinecraftPacketConstants.NETWORK_CHUNK_PUBLISHER_UPDATE) {
    lateinit var position: Float3 //blockVector3
    var radius = 4 //uVarInt

    override suspend fun encode() {
        writeBlockCoordinates(position)
        writeUnsignedVarInt(radius shl 4)
        writeInt(0) //saved chunks
    }
}