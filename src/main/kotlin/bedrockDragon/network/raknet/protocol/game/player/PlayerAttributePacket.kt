package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.type.AttributeBR

class PlayerAttributePacket: PacketPayload(MinecraftPacketConstants.UPDATE_ATTRIBUTES) {

    var runtimeEntityId = 0L
    var attributes = AttributeBR(emptyArray()).attributes

    override fun encode() {
        writeVarLong(runtimeEntityId)
        writeUnsignedVarInt(attributes.size)
        attributes.forEach {
            writeAttribute(it)
        }
        writeUnsignedVarInt(0) //stream terminator
    }
}