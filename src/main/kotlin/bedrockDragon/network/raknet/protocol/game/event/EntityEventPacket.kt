package bedrockDragon.network.raknet.protocol.game.event

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class EntityEventPacket: PacketPayload(MinecraftPacketConstants.ENTITY_EVENT) {
    var runtimeEntityId: Long = 0
    var eventId : Int = 0 //u8
    var data: Int = 0

    override suspend fun encode() {
        writeUnsignedVarLong(runtimeEntityId)
        writeByte(eventId)
        write(data)
    }
}