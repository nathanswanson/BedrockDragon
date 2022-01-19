package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class RemoveEntityPacket: PacketPayload(MinecraftPacketConstants.REMOVE_ENTITY) {
    var uniqueEntityId = -1L //sVarLong

    override fun encode() {
        writeVarLong(uniqueEntityId)
    }
}