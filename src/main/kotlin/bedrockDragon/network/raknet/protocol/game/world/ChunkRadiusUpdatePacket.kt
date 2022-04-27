package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ChunkRadiusUpdatePacket(val radius: Int): PacketPayload(MinecraftPacketConstants.CHUNK_RADIUS_UPDATED) {
    init {
        reliability = Reliability.UNRELIABLE
    }

    override suspend fun encode() {
        writeVarInt(radius)
    }
}