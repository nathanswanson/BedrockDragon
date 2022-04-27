package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ServerSettingsResponsePacket: PacketPayload(MinecraftPacketConstants.SERVER_SETTINGS_RESPONSE) {
    var formId = -1L //varLong
    var formData = ""

    override suspend fun encode() {
        writeUnsignedVarLong(formId)
        writeString(formData)
    }
}