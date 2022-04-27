package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SetLastHurtByPacket: PacketPayload(MinecraftPacketConstants.SET_LAST_HURT_BY) {
    var entityTypeId = -1 //vInt

    override suspend fun encode() {
        writeVarInt(entityTypeId)
    }
}