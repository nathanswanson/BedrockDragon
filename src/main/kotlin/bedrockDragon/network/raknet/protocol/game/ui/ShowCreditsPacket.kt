package bedrockDragon.network.raknet.protocol.game.ui

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ShowCreditsPacket: PacketPayload(MinecraftPacketConstants.SHOW_CREDITS) {
    var runtimeEntityId = -1L
    var status = 0 //sVarInt 0 start 1 end

    override suspend fun encode() {
        writeUnsignedVarLong(runtimeEntityId)
        writeVarInt(status)
    }
}