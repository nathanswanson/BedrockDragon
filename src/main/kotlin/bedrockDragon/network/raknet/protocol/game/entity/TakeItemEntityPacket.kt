package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class TakeItemEntityPacket: PacketPayload(MinecraftPacketConstants.TAKE_ITEM_ENTITY) {
    var runtimeId: Long = 0L //uVarLong
    var target: Long = 0L //uVarInt

    override fun encode() {
        writeUnsignedVarLong(target)
        writeUnsignedVarLong(runtimeId)
    }
}