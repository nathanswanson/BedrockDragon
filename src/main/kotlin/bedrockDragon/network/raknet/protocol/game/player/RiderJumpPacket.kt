package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class RiderJumpPacket: PacketPayload(MinecraftPacketConstants.RIDER_JUMP) {
    var jumpStrength = -1 //sVarInt

    override suspend fun encode() {
        writeVarInt(jumpStrength)
    }

    override fun decode(packet: Packet) {
        jumpStrength = packet.readVarInt()
    }
}