package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class InteractPacket: PacketPayload(MinecraftPacketConstants.INTERACT) {

    var actionId: UByte = 0u
    var targetRuntimeEntityId = 0L //VarLong

    override fun decode(packet: Packet) {
        actionId = packet.readUnsignedByte().toUByte()
        targetRuntimeEntityId = packet.readVarLong()
    }
}