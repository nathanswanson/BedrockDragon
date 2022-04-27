package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class UpdateEqupPacket: PacketPayload(MinecraftPacketConstants.UPDATE_EQUIP) {
    var windowId: Byte = -1
    var windowType: Byte = -1
    var unknown: Byte = -1
    var entityId = -1L //sVarLong
    //var namedTag: NBT

    override suspend fun encode() {
        writeByte(windowId)
        writeByte(windowType)
        writeByte(unknown)
        writeVarLong(entityId)
        //todo namedTag
    }
}