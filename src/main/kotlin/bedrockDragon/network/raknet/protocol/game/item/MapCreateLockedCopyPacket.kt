package bedrockDragon.network.raknet.protocol.game.item

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class MapCreateLockedCopyPacket: PacketPayload(MinecraftPacketConstants.MAP_CREATE_LOCKED_COPY) {
    var originalMapId = -1L //vLong
    var newMapId = -1L //vLong

    override suspend fun encode() {
        writeUnsignedVarLong(originalMapId)
        writeUnsignedVarLong(newMapId)
    }
}