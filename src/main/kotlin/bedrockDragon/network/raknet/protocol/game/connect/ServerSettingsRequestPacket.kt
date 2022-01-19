package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ServerSettingsRequestPacket: PacketPayload(MinecraftPacketConstants.SERVER_SETTINGS_REQUEST) {
    var formId = -1L //varLong
    var formData = ""

    override fun encode() {
        writeUnsignedVarLong(formId)
        writeString(formData)
    }
}