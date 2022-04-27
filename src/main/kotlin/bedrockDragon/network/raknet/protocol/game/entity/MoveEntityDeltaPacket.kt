package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class MoveEntityDeltaPacket: PacketPayload(MinecraftPacketConstants.MOVE_ENTITY_DELTA) {
    var runtimeEntityId = -1L //vLong
    var flags = 0 //uShort

    override suspend fun encode() {
        writeUnsignedVarLong(runtimeEntityId)
        writeUnsignedShort(flags)
    }
}