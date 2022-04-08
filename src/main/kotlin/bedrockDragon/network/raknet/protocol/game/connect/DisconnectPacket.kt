package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class DisconnectPacket: PacketPayload(MinecraftPacketConstants.DISCONNECT) {
    var kickMessage: String? = null

    override fun encode() {
        writeBoolean(kickMessage != null)
        if(kickMessage != null)
            writeString(kickMessage)
    }
}