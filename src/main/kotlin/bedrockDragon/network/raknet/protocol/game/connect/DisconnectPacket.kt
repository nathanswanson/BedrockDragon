package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class DisconnectPacket: PacketPayload(MinecraftPacketConstants.DISCONNECT) {
    var hideDisconnectScreen = false
    var kickMessage = ""

    override fun encode() {
        writeBoolean(hideDisconnectScreen)
        writeString(kickMessage)
    }
}