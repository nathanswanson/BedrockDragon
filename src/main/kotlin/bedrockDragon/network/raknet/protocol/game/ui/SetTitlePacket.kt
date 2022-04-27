package bedrockDragon.network.raknet.protocol.game.ui

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SetTitlePacket: PacketPayload(MinecraftPacketConstants.SET_TITLE) {
    var type = -1
    var text = ""
    var fadeInTime = -1 //sVarInt
    var stayTime = -1 //sVarInt
    var fadeOutTime = -1 //sVarInt

    override suspend fun encode() {
        writeVarInt(type)
        writeString(text)
        writeVarInt(fadeInTime)
        writeVarInt(stayTime)
        writeVarInt(fadeOutTime)
    }
}