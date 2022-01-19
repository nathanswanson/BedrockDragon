package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SetLocalPlayerAsInitializedPacket: PacketPayload(MinecraftPacketConstants.SET_LOCAL_PLAYER_AS_INITIALIZED) {
    var runtimeEntityId = -1L //uVarLong

    override fun decode(packet: Packet) {
        runtimeEntityId = packet.readUnsignedVarLong()
    }
}