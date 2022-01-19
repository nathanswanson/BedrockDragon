package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class AnimatePacket: PacketPayload(MinecraftPacketConstants.ANIMATE) {
    var actionId = -1 //sVarInt
    var runtimeEntityId = -1L //vLong

    override fun encode() {
        writeVarInt(actionId)
        writeUnsignedVarLong(runtimeEntityId)
    }

    override fun decode(packet: Packet) {
        actionId = packet.readVarInt()
        runtimeEntityId = packet.readUnsignedVarLong()
    }

}