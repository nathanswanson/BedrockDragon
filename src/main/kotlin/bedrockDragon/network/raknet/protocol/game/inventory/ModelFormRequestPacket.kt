package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ModelFormRequestPacket: PacketPayload(MinecraftPacketConstants.MODEL_FORM_REQUEST) {
    var formId = -1 //varInt
    var formData = ""

    override suspend fun encode() {
        writeVarInt(formId)
        writeString(formData)
    }
}