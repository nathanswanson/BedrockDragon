package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ShowCreditsPacket: PacketPayload(MinecraftPacketConstants.SHOW_CREDITS){
    var runtimeEntityId = -1L //vLong
    var status = -1L //sVarLong

    override fun encode() {
        writeUnsignedVarLong(runtimeEntityId)
        writeVarLong(status)
    }
}