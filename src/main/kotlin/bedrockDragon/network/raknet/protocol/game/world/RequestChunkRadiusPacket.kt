package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class RequestChunkRadiusPacket: PacketPayload(MinecraftPacketConstants.REQUEST_CHUNK_RADIUS) {
    var chunkRadius = 8 //sVarInt

    override fun decode(packet: Packet) {
        chunkRadius = readVarInt()
    }
}