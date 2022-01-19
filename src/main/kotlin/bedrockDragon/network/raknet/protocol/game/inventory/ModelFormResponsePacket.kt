package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ModelFormResponsePacket: PacketPayload(MinecraftPacketConstants.MODEL_FORM_RESPONSE) {
    var formId = -1 //varInt
    var formData = ""

    override fun decode(packet: Packet) {
        formId = packet.readVarInt()
        formData = packet.readString()
    }
}